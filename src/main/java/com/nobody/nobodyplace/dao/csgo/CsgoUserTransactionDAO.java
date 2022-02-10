package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.entity.csgo.CsgoUserTransactionKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CsgoUserTransactionDAO extends JpaRepository<CsgoUserTransaction, CsgoUserTransactionKey> {
    List<CsgoUserTransaction> getCsgoUserTransactionsByTransactTimeBetween(int begin, int end);

    List<CsgoUserTransaction> getCsgoUserTransactionsByTransactTimeLessThanAndTransactTypeEquals(int end, byte type);
}