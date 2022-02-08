package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoUserProperty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoUserPropertyDAO extends JpaRepository<CsgoUserProperty, Integer> {

}