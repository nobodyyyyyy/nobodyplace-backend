package com.nobody.nobodyplace.dao.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CsgoItemDAO extends JpaRepository<CsgoItem, Integer> {
    List<CsgoItem> getCsgoItemsByItemIdIn(List<Integer> ids);
}