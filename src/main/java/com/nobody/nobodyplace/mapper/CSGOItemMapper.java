package com.nobody.nobodyplace.mapper;

import com.github.pagehelper.Page;
import com.nobody.nobodyplace.pojo.dto.*;
import com.nobody.nobodyplace.pojo.entity.CSGOInventoryItem;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CSGOItemMapper {

    Page<CSGOItem> getByFilterInfo(CSGOItemPageQueryDTO csgoItemPageQueryDTO);

    void insertInventoryItem(CSGOInventoryItem csgoInventoryItem);

    Page<CSGOInventoryVO> getInventoryItem(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO);

    List<CSGOInventoryVO> getUserAllInventory(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO);

    void deleteInventoryItem(CSGODeleteInventoryDTO csgoDeleteInventoryDTO);

    void insertItemHistoryPrices(@Param("historyPrices") List<CSGOItemHistoryPriceDTO> csgoItemHistoryPriceDTOS);

    List<CSGOItemHistoryPriceDTO> getItemHistoryPrices(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO);

    CSGOItemHistoryPriceDTO getItemLatestPrice(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO);

    List<CSGOItemHistoryPriceDTO> getItemHistoryPricesRecent(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO);

}
