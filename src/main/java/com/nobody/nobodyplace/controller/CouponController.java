package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.pojo.dto.CouponInfoDTO;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CouponService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAsync
@CrossOrigin
public class CouponController {

    final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping(API.GET_SEC_KILL_COUPON_INFO)
    public Result<CouponInfoDTO> getCouponInfo(@RequestParam Long couponId) {
        return Result.success(couponService.getCouponInfo(couponId));
    }

    @PostMapping(API.SEC_KILL_REQUEST + "{couponId}")
    public Result secKillCoupon(@PathVariable Long couponId) {
        return couponService.secKillCoupon(couponId);
    }
}
