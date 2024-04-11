package com.nobody.nobodyplace.service.impl;

import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.mapper.CouponMapper;
import com.nobody.nobodyplace.pojo.dto.CouponInfoDTO;
import com.nobody.nobodyplace.pojo.entity.CouponOrder;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CouponService;
import com.nobody.nobodyplace.utils.redis.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    final CouponMapper couponMapper;
    final RedisIdWorker redisIdWorker;

    final static String couponOrderIdKey = "seckill_order";

    public CouponServiceImpl(CouponMapper couponMapper, RedisIdWorker redisIdWorker) {
        this.couponMapper = couponMapper;
        this.redisIdWorker = redisIdWorker;
    }

    @Override
    public CouponInfoDTO getCouponInfo(Long id) {
        return couponMapper.getCouponInfoById(id);
    }

    @Override
    public Result secKillCoupon(Long id) {
        //        Long currentUser = BaseContext.getCurrentId();
        Long currentUser = 1L; // todo 测试，目前没加拦截器
        log.info("secKillCoupon... [Before] user [" + currentUser + "] want to sec kill coupon: " + id);
        CouponInfoDTO couponInfo = couponMapper.getCouponInfoById(id);
        if (couponInfo.getStartTime().isAfter(LocalDateTime.now())) {
            return Result.error("秒杀没有开始");
        }
        if (couponInfo.getStock() < 1) {
            return Result.error("库存不足");
        }
        // 事务提交了，才保证锁释放
        synchronized (currentUser.toString().intern()) {
            // 获取代理的对象（事务的代理对象）
            Object proxy = AopContext.currentProxy(); // 拿到代理对象 CouponService 接口
            CouponServiceImpl s = (CouponServiceImpl) proxy;
            return s.createCouponOrder(couponInfo.getId(), currentUser);
        }
    }

    @Transactional
    public Result createCouponOrder(Long couponId, Long currentUser) {
        // 一人一单
        int bought = couponMapper.queryUserOrderedCoupon(couponId, currentUser);
        if (bought > 0) {
            return Result.error("不能重复购买");
        }
        int success = couponMapper.updateCouponStockMinusOne(couponId);
        if (success == 0) {
            return Result.error("库存不足");
        }
        // 创建订单。在订单表新增数据
        long orderId = redisIdWorker.nextId(couponOrderIdKey);
        couponMapper.generateOrder(CouponOrder.builder()
                .id(orderId)
                .couponId(couponId)
                .userId(currentUser)
                .status(0)
                .build());
        log.info("secKillCoupon... [Done] user [" + currentUser + "] sec kill coupon: " + couponId);
        return Result.success(couponId);
    }
}
