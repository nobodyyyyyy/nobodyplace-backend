package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.entity.csgo.CsgoUserTransactionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoUserTransactionDAO extends JpaRepository<CsgoUserTransaction, CsgoUserTransactionKey> {
}