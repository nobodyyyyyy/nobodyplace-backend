package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.pojo.dto.*;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryVO;
import com.nobody.nobodyplace.pojo.vo.CSGORankingVO;
import com.nobody.nobodyplace.response.PageResult;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Future;

public interface CSGOItemService {

    PageResult pageQuery(CSGOItemPageQueryDTO csgoItemPageQueryDTO);

    void addUserItem(CSGOAddUserItemDTO csgoAddUserItemDTO);

    PageResult getUserInventory(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO);

    List<CSGOInventoryVO> getUserAllInventory(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO);

    Float deleteUserInventory(CSGODeleteInventoryDTO csgoDeleteInventoryDTO);

    Future<List<CSGOItemHistoryPriceDTO>> requestItemPrices(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO) throws MalformedURLException, InterruptedException;

    void insertItemPrices(List<CSGOItemHistoryPriceDTO> csgoItemHistoryPriceDTOS);

    List<CSGOItemHistoryPriceDTO> getItemHistoryPrices(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO);

    CSGOItemHistoryPriceDTO getItemLatestPrice(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO);

    List<CSGOItemHistoryPriceDTO> getItemHistoryPricesRecent(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO);

    List<CSGORankingVO> getRanking();

    void notifyUser(String userId, String msg);

}
