package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsgoItemDAO extends JpaRepository<CsgoItem, Integer> {
    int deleteByPrimaryKey(Integer itemId);

    int insert(CsgoItem record);

    int insertSelective(CsgoItem record);

    CsgoItem selectByPrimaryKey(Integer itemId);

    int updateByPrimaryKeySelective(CsgoItem record);

    int updateByPrimaryKey(CsgoItem record);
}