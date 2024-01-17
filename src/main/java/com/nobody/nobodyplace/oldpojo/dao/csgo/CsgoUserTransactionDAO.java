package com.nobody.nobodyplace.oldpojo.dao.csgo;

import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoUserTransactionKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CsgoUserTransactionDAO extends JpaRepository<CsgoUserTransaction, CsgoUserTransactionKey> {
    List<CsgoUserTransaction> getCsgoUserTransactionsByTransactTimeBetween(int begin, int end);

    List<CsgoUserTransaction> getCsgoUserTransactionsByTransactTimeLessThanAndTransactTypeEquals(int end, byte type);
}
