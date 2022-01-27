package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoUserProperty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoUserPropertyDAO extends JpaRepository<CsgoUserProperty, Integer> {
    int deleteByPrimaryKey(Integer itemId);

    int insert(CsgoUserProperty record);

    int insertSelective(CsgoUserProperty record);

    CsgoUserProperty selectByPrimaryKey(Integer itemId);

    int updateByPrimaryKeySelective(CsgoUserProperty record);

    int updateByPrimaryKey(CsgoUserProperty record);
}