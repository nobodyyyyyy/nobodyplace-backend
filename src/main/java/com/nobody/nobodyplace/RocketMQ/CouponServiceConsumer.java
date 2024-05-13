package com.nobody.nobodyplace.RocketMQ;

import com.google.gson.Gson;
import com.nobody.nobodyplace.pojo.entity.CouponOrder;
import com.nobody.nobodyplace.service.impl.CouponServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.nobody.nobodyplace.service.impl.CouponServiceImpl.MQ_TAG_COUPON;

@Component
@Slf4j
public class CouponServiceConsumer {

    public static final String TOPIC = "NOBODY_PLACE_TOPIC";

    final CouponServiceImpl couponService;

    public CouponServiceConsumer(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }


    @Service
    @RocketMQMessageListener(topic = TOPIC, selectorExpression = MQ_TAG_COUPON, consumerGroup = "Con_Group_Coupon")
    public class OrderCreateConsumer implements RocketMQListener<String> {
        // 监听到消息就会执行此方法
        @Override
        public void onMessage(String order) {
            CouponOrder couponOrder = new Gson().fromJson(order, CouponOrder.class);
            couponService.createCouponOrder(couponOrder);
        }
    }
}
