package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistory;
import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoPriceHistoryDAO extends JpaRepository<CsgoPriceHistory, CsgoPriceHistoryKey> {
    
}