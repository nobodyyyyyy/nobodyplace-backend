package com.nobody.nobodyplace.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.nobody.nobodyplace.RocketMQ.MQConsumerService;
import com.nobody.nobodyplace.RocketMQ.MQProducerService;
import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.mapper.CouponMapper;
import com.nobody.nobodyplace.pojo.dto.CouponInfoDTO;
import com.nobody.nobodyplace.pojo.entity.CouponOrder;
import com.nobody.nobodyplace.pojo.entity.User;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CouponService;
import com.nobody.nobodyplace.utils.redis.RedisIdWorker;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    final CouponMapper couponMapper;
    final RedisIdWorker redisIdWorker;

    final StringRedisTemplate stringRedisTemplate;

    private BlockingQueue<CouponOrder> orderBlockingQueue = new ArrayBlockingQueue<>(1024 * 1024);

    private final ExecutorService constructingOrdersExecutor = Executors.newSingleThreadExecutor();

    private CouponServiceImpl proxy;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private MQProducerService mqProducerService;

    private final static String COUPON_ORDER_ID_KEY = "seckill:order:";

    private final static String COUPON_ORDER_ID_GENERATOR_KEY = "seckill_order";

    private final static String SECKILL_STOCK_KEY = "seckill:stock:";

    private final static String STREAM_QUEUE_NAME = "streams.order";

    private static final DefaultRedisScript<Long> SECKILL_PREHANDLE_SCRIPT;

    public static final String MQ_TAG_COUPON = "coupon";

    static {
        SECKILL_PREHANDLE_SCRIPT = new DefaultRedisScript<>();
        SECKILL_PREHANDLE_SCRIPT.setLocation(new ClassPathResource("seckill_prehandle.lua"));
        SECKILL_PREHANDLE_SCRIPT.setResultType(Long.class);
    }

    public CouponServiceImpl(CouponMapper couponMapper, RedisIdWorker redisIdWorker, StringRedisTemplate stringRedisTemplate) {
        this.couponMapper = couponMapper;
        this.redisIdWorker = redisIdWorker;
        this.stringRedisTemplate = stringRedisTemplate;
        preheatRedis();
    }

    /**
     * 预热 Redis，将所有秒杀券的个数更新进 Redis 中去
     */
    private void preheatRedis() {
        List<CouponInfoDTO> infos = couponMapper.getAllSeckillCouponInfo();
        for (CouponInfoDTO info : infos) {
            stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + info.getId(), String.valueOf(info.getStock()));
        }
    }

//    @PostConstruct
//    private void init() {
//        constructingOrdersExecutor.submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
////                        CouponOrder couponOrder = orderBlockingQueue.take();
//                        CouponOrder couponOrder = CouponOrder.builder().build();
//                        // 1 获取消息队列订单信息
//                        // XREADGROUP GROUP g1 c1 [g1 组的 c1 消费者] COUNT 1 BLOCK 2000 STREAMS streams.order > [> 代表最近一条未消费]
//                        List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
//                                Consumer.from("g1", "c1"),
//                                StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                                StreamOffset.create(STREAM_QUEUE_NAME, ReadOffset.lastConsumed())
//                        );
//                        // 2 判断消息获取是否成功
//                        if (list == null || list.isEmpty()) {
//                            continue;
//                        }
//                        // 3 获取成功，可以下单  count 1 所以拿 0
//                        MapRecord<String, Object, Object> record = list.get(0);  // <消息id, key, value >
//                        Map<Object, Object> values = record.getValue();
//                        for (Map.Entry<Object, Object> e : values.entrySet()) {
//                            if (e.getKey().toString().equals("userId")) {
//                                couponOrder.setUserId(Long.valueOf(String.valueOf(e.getValue())));
//                            } else if (e.getKey().toString().equals("couponId")) {
//                                couponOrder.setCouponId(Long.valueOf(String.valueOf(e.getValue())));
//                            } else if (e.getKey().toString().equals("id")) {
//                                couponOrder.setId(Long.valueOf(String.valueOf(e.getValue())));
//                            }
//                        }
//                        // 下单
//                        createCouponOrder(couponOrder);
//                        // 4 ACK 确认 SACK stream.oders g1 id
//                        stringRedisTemplate.opsForStream().acknowledge(STREAM_QUEUE_NAME, "g1", record.getId());
//                    } catch (Exception e) {
//                        // 消息到 pending list 了，要去 pending list 处理
//                        log.info("handleConstructingOrders... err: {}", e.getMessage());
//                        handlePendingList();
//                    }
//                }
//            }
//
//            private void handlePendingList() {
//                while (true) {
//                    try {
//                        CouponOrder couponOrder = CouponOrder.builder().build();
//                        // 1 获取 pending list 订单信息
//                        // XREADGROUP GROUP g1 c1 [g1 组的 c1 消费者] COUNT 1 BLOCK 2000 STREAMS streams.order 0 【0 代表读 pending list】
//                        List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
//                                Consumer.from("g1", "c1"),
//                                StreamReadOptions.empty().count(1),
//                                StreamOffset.create(STREAM_QUEUE_NAME, ReadOffset.from("0"))
//                        );
//                        // 2 判断消息获取是否成功
//                        if (list == null || list.isEmpty()) {
//                            // pending list 没消息
//                            return;
//                        }
//                        // 3 获取成功，可以下单  count 1 所以拿 0
//                        MapRecord<String, Object, Object> record = list.get(0);  // <消息id, key, value >
//                        Map<Object, Object> values = record.getValue();
//                        for (Map.Entry<Object, Object> e : values.entrySet()) {
//                            if (e.getKey().toString().equals("userId")) {
//                                couponOrder.setUserId(Long.valueOf(String.valueOf(e.getValue())));
//                            } else if (e.getKey().toString().equals("couponId")) {
//                                couponOrder.setCouponId(Long.valueOf(String.valueOf(e.getValue())));
//                            } else if (e.getKey().toString().equals("id")) {
//                                couponOrder.setId(Long.valueOf(String.valueOf(e.getValue())));
//                            }
//                        }
//                        // 下单
//                        createCouponOrder(couponOrder);
//                        // 4 ACK 确认 SACK stream.oders g1 id
//                        stringRedisTemplate.opsForStream().acknowledge(STREAM_QUEUE_NAME, "g1", record.getId());
//                    } catch (Exception e) {
////                        log.info("再次尝试出错了");
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                    }
//                }
//            }
//        });
//    }

    private void createCouponOrder(CouponOrder couponOrder) {

        long currentUser = couponOrder.getUserId();
        long couponId = couponOrder.getCouponId();

        // 下面不行，子线程不能从 ThreadLocal 中拿到代理对象
        // 怎么处理事务呢？要提前处理事务！
        //
//                        Object proxy = AopContext.currentProxy(); // 拿到代理对象 CouponService 接口
//                        CouponServiceImpl s = (CouponServiceImpl) proxy;
//                        s.createCouponOrder(couponId, currentUser);
        RLock lock = redissonClient.getLock("lock:order:" + currentUser);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            // 理应不会发生，因为前面加入队列前都判断了。这里兜底
            log.info("不允许重复下单");
            return;
        }
        try {
            this.proxy.innerCreateCouponOrder(couponOrder);
        } finally {
            lock.unlock();
        }
    }

//    @Service
//    @RocketMQMessageListener(topic = MQConsumerService.TOPIC, selectorExpression = MQ_TAG_COUPON, consumerGroup = "Con_Group_One")
//    public class ConsumerSend implements RocketMQListener<String> {
//        // 监听到消息就会执行此方法
//        @Override
//        public void onMessage(String order) {
//            CouponOrder couponOrder = new Gson().fromJson(order, CouponOrder.class);
//            log.info("接收到消息了");
//        }
//    }

    @Override
    public CouponInfoDTO getCouponInfo(Long id) {
        log.info("getCouponInfo... Checking coupon info " + id);
        return couponMapper.getCouponInfoById(id);
    }

    /**
     * 最后决定使用 RocketMQ + Redis + Lua 进行下单的订单生成，RocketMQ 是为了异步下单，Redis + Lua 是为了加速判断
     * @param couponId
     * @return
     */
    @Override
    public Result secKillCoupon(Long couponId) {
        Long currentUser = BaseContext.getCurrentId();
        long orderId = redisIdWorker.nextId(COUPON_ORDER_ID_GENERATOR_KEY);
        Long res = stringRedisTemplate.execute(SECKILL_PREHANDLE_SCRIPT,
                Collections.emptyList(),
                String.valueOf(couponId),
                String.valueOf(currentUser),
                String.valueOf(orderId));
        if (res == null) {
            return Result.error("内部错误");
        }
        if (res.equals(1L)) {
            return Result.error("库存不足");
        } else if (res.equals(2L)) {
            return Result.error("不能重复购买");
        }

        // 向 MQ 发送消息
        CouponOrder order = new CouponOrder(currentUser, couponId, orderId);
        mqProducerService.send(JSON.toJSONString(order), MQ_TAG_COUPON);
        if (this.proxy == null) {
            this.proxy = (CouponServiceImpl) AopContext.currentProxy(); // 拿到代理对象 CouponService 接口
        }
        return Result.success(orderId);
    }

    // 使用 阻塞队列
//    @Override
//    public Result secKillCoupon(Long couponId) {
//        Long currentUser = BaseContext.getCurrentId();
//        Long res = stringRedisTemplate.execute(SECKILL_PREHANDLE_SCRIPT,
//                        Collections.emptyList(),
//                        String.valueOf(couponId),
//                        String.valueOf(currentUser));
//        if (res == null) {
//            return Result.error("内部错误");
//        }
//        if (res.equals(1L)) {
//            return Result.error("库存不足");
//        } else if (res.equals(2L)) {
//            return Result.error("不能重复购买");
//        }
//
//        // 保存订单到阻塞队列
//        long orderId = redisIdWorker.nextId(COUPON_ORDER_ID_GENERATOR_KEY);
//        CouponOrder order = CouponOrder.builder().id(orderId).couponId(couponId).userId(currentUser).status(0).build();
//        orderBlockingQueue.add(order);
//        this.proxy = (CouponServiceImpl) AopContext.currentProxy(); // 拿到代理对象 CouponService 接口
//        return Result.success(orderId);
//    }

//    @Override
    @Deprecated
    public Result oldSecKillCoupon(Long id) {
        Long currentUser = BaseContext.getCurrentId();
//        Long currentUser = 1L; // todo 测试，目前没加拦截器
        log.info("secKillCoupon... [Before] user [" + currentUser + "] want to sec kill coupon: " + id);
        CouponInfoDTO couponInfo = couponMapper.getCouponInfoById(id);
        if (couponInfo.getStartTime().isAfter(LocalDateTime.now())) {
            return Result.error("秒杀没有开始");
        }

        if (couponInfo.getStock() < 1) {
            return Result.error("库存不足");
        }
//        // 事务提交了，才保证锁释放
//        synchronized (currentUser.toString().intern()) {
//            // 获取代理的对象（事务的代理对象）
//            Object proxy = AopContext.currentProxy(); // 拿到代理对象 CouponService 接口
//            CouponServiceImpl s = (CouponServiceImpl) proxy;
//            return s.createCouponOrder(couponInfo.getId(), currentUser);
//        }
//        SimpleRedisLock lock = new SimpleRedisLock(redisTemplate, "order:" + currentUser);
//        boolean islock = lock.tryLock(1000);

        RLock lock = redissonClient.getLock("lock:order:" + currentUser);
        boolean islock = lock.tryLock();
        if (!islock) {
            // 重试 或者返回错误信息
            // 业务：并发下单，避免一个人重复下单
            return Result.error("请勿重复下单");
        }
//
//        // 获取代理的对象（事务的代理对象）
        try {
            Object proxy = AopContext.currentProxy(); // 拿到代理对象 CouponService 接口
            CouponServiceImpl s = (CouponServiceImpl) proxy;
            return s.oldCreateCouponOrder(couponInfo.getId(), currentUser);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @Deprecated
    public Result oldCreateCouponOrder(Long couponId, Long currentUser) {
        int bought = couponMapper.queryUserOrderedCoupon(couponId, currentUser);
        if (bought > 0) {
            return Result.error("不能重复购买");
        }
        int success = couponMapper.updateCouponStockMinusOne(couponId);
        if (success == 0) {
            return Result.error("库存不足");
        }
        // 创建订单。在订单表新增数据
        long orderId = redisIdWorker.nextId(COUPON_ORDER_ID_KEY);
        couponMapper.generateOrder(CouponOrder.builder()
                .id(orderId)
                .couponId(couponId)
                .userId(currentUser)
                .status(0)
                .build());
        log.info("secKillCoupon... [Done] user [" + currentUser + "] sec kill coupon: " + couponId);
        return Result.success(couponId);
    }

    @Transactional
    public void innerCreateCouponOrder(CouponOrder order) {
        long currentUser = order.getUserId();
        long couponId = order.getCouponId();
        int bought = couponMapper.queryUserOrderedCoupon(couponId, currentUser);
        if (bought > 0) {
            log.info("不能重复购买");
            return;
        }
        int success = couponMapper.updateCouponStockMinusOne(couponId);
        if (success == 0) {
            log.info("库存不足");
            return;
        }
        // 创建订单。在订单表新增数据
        couponMapper.generateOrder(order);
        log.info("innerCreateCouponOrder... [Done] user [" + currentUser + "] sec kill coupon: " + couponId);
    }
}
