package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.pojo.dto.CSGOAddUserItemDTO;
import com.nobody.nobodyplace.pojo.dto.CSGODeleteInventoryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOInventoryPageQueryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.response.PageResult;

public interface CSGOItemService {

    PageResult pageQuery(CSGOItemPageQueryDTO csgoItemPageQueryDTO);

    void addUserItem(CSGOAddUserItemDTO csgoAddUserItemDTO);

    PageResult getUserInventory(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO);

    void deleteUserInventory(CSGODeleteInventoryDTO csgoDeleteInventoryDTO);

}
