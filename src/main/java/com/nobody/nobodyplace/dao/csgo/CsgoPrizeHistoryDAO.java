package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoPrizeHistory;
import com.nobody.nobodyplace.entity.csgo.CsgoPrizeHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoPrizeHistoryDAO extends JpaRepository<CsgoPrizeHistory, CsgoPrizeHistoryKey> {

}