package com.nobody.nobodyplace.oldpojo.dao.csgo;

import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CsgoItemDAO extends JpaRepository<CsgoItem, Integer> {
    List<CsgoItem> getCsgoItemsByItemIdIn(List<Integer> ids);
}
