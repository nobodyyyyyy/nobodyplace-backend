package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoItemDAO extends JpaRepository<CsgoItem, Integer> {

}