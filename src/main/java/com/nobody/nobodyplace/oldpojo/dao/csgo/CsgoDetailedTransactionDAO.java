package com.nobody.nobodyplace.oldpojo.dao.csgo;

import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoDetailedTransaction;
import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoDetailedTransactionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoDetailedTransactionDAO extends JpaRepository<CsgoDetailedTransaction, CsgoDetailedTransactionKey> {

}
