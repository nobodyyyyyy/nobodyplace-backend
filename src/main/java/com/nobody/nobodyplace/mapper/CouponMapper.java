package com.nobody.nobodyplace.mapper;

import com.nobody.nobodyplace.pojo.dto.CouponInfoDTO;
import com.nobody.nobodyplace.pojo.entity.CouponOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {

    CouponInfoDTO getCouponInfoById(Long id);

    List<CouponInfoDTO> getAllSeckillCouponInfo();

    Integer updateCouponStockMinusOne(Long id);

    Integer queryUserOrderedCoupon(Long couponId, Long userId);

    void generateOrder(CouponOrder order);
}
