package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistory;
import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CsgoPriceHistoryDAO extends JpaRepository<CsgoPriceHistory, CsgoPriceHistoryKey> {

    /**
     * 查询某时间范围内商品的出售历史记录
     * @param id 商品 id
     * @param begin 开始时间戳，秒
     * @param end 结束时间戳，秒
     * @return 结果 list
     */
    List<CsgoPriceHistory> findAllByItemIdEqualsAndTransactTimeBetweenOrderByTransactTime(Integer id, Integer begin, Integer end);
}