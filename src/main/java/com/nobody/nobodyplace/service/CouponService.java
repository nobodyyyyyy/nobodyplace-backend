package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.pojo.dto.CouponInfoDTO;
import com.nobody.nobodyplace.response.Result;

public interface CouponService {

    CouponInfoDTO getCouponInfo(Long id);

    Result secKillCoupon(Long id);
}
