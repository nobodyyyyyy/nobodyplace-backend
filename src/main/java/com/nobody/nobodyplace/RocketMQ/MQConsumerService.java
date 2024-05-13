//package com.nobody.nobodyplace.RocketMQ;
//
//import com.google.gson.Gson;
//import com.nobody.nobodyplace.pojo.entity.CouponOrder;
//import com.nobody.nobodyplace.pojo.entity.User;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import static com.nobody.nobodyplace.service.impl.CouponServiceImpl.MQ_TAG_COUPON;
//
//@Slf4j
//@Component
//public class MQConsumerService {
//
//    public static final String TOPIC = "NOBODY_PLACE_TOPIC";
//
//    // topic需要和生产者的topic一致，consumerGroup属性是必须指定的，内容可以随意
//    // selectorExpression的意思指的就是tag，默认为“*”，不设置的话会监听所有消息
//    @Service
//    @RocketMQMessageListener(topic = TOPIC, selectorExpression = "tag1", consumerGroup = "Con_Group_One")
//    public class ConsumerSend implements RocketMQListener<User> {
//        // 监听到消息就会执行此方法
//        @Override
//        public void onMessage(User user) {
////            log.info("1监听到消息：user={}", JSON.toJSONString(user));
//        }
//    }
//
//    @Service
//    @RocketMQMessageListener(topic = TOPIC, selectorExpression = MQ_TAG_COUPON, consumerGroup = "Con_Group_Coupon")
//    public class ConsumerSend2 implements RocketMQListener<String> {
//        // 监听到消息就会执行此方法
//        @Override
//        public void onMessage(String order) {
//            CouponOrder couponOrder = new Gson().fromJson(order, CouponOrder.class);
//            log.info("接收到消息了");
//        }
//    }
//
//}
