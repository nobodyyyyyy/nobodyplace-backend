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
}
