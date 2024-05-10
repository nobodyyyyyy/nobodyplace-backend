package com.nobody.nobodyplace.pojo.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponOrder {

    Long id;
    Long userId;
    Long couponId;
    Integer status; // 0 未支付 1 已支付

    public CouponOrder() {}

    public CouponOrder(Long id, Long userId, Long couponId) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
    }

    public CouponOrder(Long id, Long userId, Long couponId, Integer status) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
    }
}
