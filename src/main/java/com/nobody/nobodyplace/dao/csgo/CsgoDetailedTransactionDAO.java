package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoDetailedTransaction;
import com.nobody.nobodyplace.entity.csgo.CsgoDetailedTransactionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoDetailedTransactionDAO extends JpaRepository<CsgoDetailedTransaction, CsgoDetailedTransactionKey> {

}