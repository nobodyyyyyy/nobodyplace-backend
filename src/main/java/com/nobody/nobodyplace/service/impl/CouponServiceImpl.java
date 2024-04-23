package com.nobody.nobodyplace.service.impl;

import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.mapper.CouponMapper;
import com.nobody.nobodyplace.pojo.dto.CouponInfoDTO;
import com.nobody.nobodyplace.pojo.entity.CouponOrder;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CouponService;
import com.nobody.nobodyplace.utils.redis.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private final static String COUPON_ORDER_ID_KEY = "seckill:order:";

    private final static String COUPON_ORDER_ID_GENERATOR_KEY = "seckill_order";

    private final static String SECKILL_STOCK_KEY = "seckill:stock:";

    private static final DefaultRedisScript<Long> SECKILL_PREHANDLE_SCRIPT;

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

    @PostConstruct
    private void init() {
        constructingOrdersExecutor.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        CouponOrder couponOrder = orderBlockingQueue.take();
                        createCouponOrder(couponOrder);
                    } catch (Exception e) {
                        log.info("handleConstructingOrders... err: {}", e.getMessage());
                    }
                }
            }
        });
    }

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

    @Override
    public CouponInfoDTO getCouponInfo(Long id) {
        log.info("getCouponInfo... Checking coupon info " + id);
        return couponMapper.getCouponInfoById(id);
    }

    @Override
    public Result secKillCoupon(Long couponId) {
        Long currentUser = BaseContext.getCurrentId();
        Long res = stringRedisTemplate.execute(SECKILL_PREHANDLE_SCRIPT,
                        Collections.emptyList(),
                        String.valueOf(couponId),
                        String.valueOf(currentUser));
        if (res == null) {
            return Result.error("内部错误");
        }
        if (res.equals(1L)) {
            return Result.error("库存不足");
        } else if (res.equals(2L)) {
            return Result.error("不能重复购买");
        }

        // 保存订单到阻塞队列
        long orderId = redisIdWorker.nextId(COUPON_ORDER_ID_GENERATOR_KEY);
        CouponOrder order = CouponOrder.builder().id(orderId).couponId(couponId).userId(currentUser).status(0).build();
        orderBlockingQueue.add(order);
        this.proxy = (CouponServiceImpl) AopContext.currentProxy(); // 拿到代理对象 CouponService 接口
        return Result.success(orderId);
    }

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
