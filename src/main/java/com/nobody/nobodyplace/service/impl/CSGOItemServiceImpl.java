package com.nobody.nobodyplace.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.gson.csgo.MarketHistoryItemInfoResponse;
import com.nobody.nobodyplace.mapper.CSGOItemMapper;
import com.nobody.nobodyplace.pojo.dto.*;
import com.nobody.nobodyplace.pojo.entity.CSGOInventoryItem;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryVO;
import com.nobody.nobodyplace.pojo.vo.CSGORankingVO;
import com.nobody.nobodyplace.properties.JwtProperties;
import com.nobody.nobodyplace.response.PageResult;
import com.nobody.nobodyplace.service.CSGOItemService;
import com.nobody.nobodyplace.service.WebSocketServer;
import com.nobody.nobodyplace.utils.HttpUtil;
import com.nobody.nobodyplace.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class CSGOItemServiceImpl implements CSGOItemService {

    final CSGOItemMapper csgoItemMapper;

    private final JwtProperties jwtProperties;

    private static final String API_GET_ITEM_PAST_7_DAY_TRANSACTION = "https://buff.163.com/api/market/goods/price_history/buff?game=csgo&currency=CNY&days=7";
    private static final String API_GET_ITEM_PAST_MONTH_TRANSACTION = "https://buff.163.com/api/market/goods/price_history/buff?game=csgo&currency=CNY&days=30";

    public static volatile Semaphore maxParallelRequest = new Semaphore(5);

    public CSGOItemServiceImpl(CSGOItemMapper csgoItemMapper, JwtProperties jwtProperties) {
        this.csgoItemMapper = csgoItemMapper;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public PageResult pageQuery(CSGOItemPageQueryDTO csgoItemPageQueryDTO) {
        PageHelper.startPage(csgoItemPageQueryDTO.getPage(), csgoItemPageQueryDTO.getPageSize());
        Page<CSGOItem> page = csgoItemMapper.getByFilterInfo(csgoItemPageQueryDTO);
        long total = page.getTotal();
        List<CSGOItem> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void addUserItem(CSGOAddUserItemDTO csgoAddUserItemDTO) {
        CSGOInventoryItem item = new CSGOInventoryItem();
        BeanUtils.copyProperties(csgoAddUserItemDTO, item);
        item.setBoughtTime(TimeUtil.strToLocalDateTime(csgoAddUserItemDTO.getBoughtDate(), TimeUtil.NORMAL_FORMAT_PATTERN));
        item.setUpdateTime(LocalDateTime.now());
        item.setUserId(BaseContext.getCurrentId());  // 线程池拿当前操作的 userid
        csgoItemMapper.insertInventoryItem(item);
    }

    @Override
    public PageResult getUserInventory(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO) {
        PageHelper.startPage(csgoInventoryPageQueryDTO.getPage(), csgoInventoryPageQueryDTO.getPageSize());
        Page<CSGOInventoryVO> page = csgoItemMapper.getInventoryItem(csgoInventoryPageQueryDTO);
        long total = page.getTotal();
        List<CSGOInventoryVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public List<CSGOInventoryVO> getUserAllInventory(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO) {
        return csgoItemMapper.getUserAllInventory(csgoInventoryPageQueryDTO);
    }

    @Override
    public Float deleteUserInventory(CSGODeleteInventoryDTO csgoDeleteInventoryDTO) {
        Float boughtPrice = csgoItemMapper.getInventoryItemBoughtPrice(csgoDeleteInventoryDTO);
        csgoItemMapper.deleteInventoryItem(csgoDeleteInventoryDTO);
        return boughtPrice;
    }

    /**
     * 获取一个商品的真实交易历史记录。走网络请求
     */
    @Override
    @Async("taskExecutor")
    public Future<List<CSGOItemHistoryPriceDTO>> requestItemPrices(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO) throws MalformedURLException, InterruptedException {
        maxParallelRequest.acquire();
        log.info("requestItemPrices... Web current price request for {}", csgoItemHistoryPriceQueryDTO);
        long itemId = csgoItemHistoryPriceQueryDTO.getItemId();
        String requestUrl = HttpUtil.setUrlParam(API_GET_ITEM_PAST_MONTH_TRANSACTION, "goods_id", itemId);
        String cookie = jwtProperties.getBuffCookie();
        HttpUtil.HttpResponse response = HttpUtil.get(new URL(requestUrl), false, 10000, cookie);
        MarketHistoryItemInfoResponse historyItemInfoResponse = null;
        try {
            historyItemInfoResponse = new Gson().fromJson(response.data, MarketHistoryItemInfoResponse.class);
        } catch (Exception e) {
            log.info("requestItemPrices... Request error for query item id: {}", csgoItemHistoryPriceQueryDTO.getItemId());
        }
        if (historyItemInfoResponse != null) {
            List<CSGOItemHistoryPriceDTO> histories = historyItemInfoResponse.getPriceHistory(csgoItemHistoryPriceQueryDTO.getItemId());
            Thread.sleep(1000);
            maxParallelRequest.release();
            return new AsyncResult<>(histories);
        } else {
            maxParallelRequest.release();
            return null;
        }

    }

    /**
     * 历史价格写 DB
     */
    @Override
    public void insertItemPrices(List<CSGOItemHistoryPriceDTO> csgoItemHistoryPriceDTOS) {
        try {
            if (csgoItemHistoryPriceDTOS != null && csgoItemHistoryPriceDTOS.size() > 0 && csgoItemHistoryPriceDTOS.get(0) != null) {
                log.info("insertItemPrices... Inserting item {}", csgoItemHistoryPriceDTOS.get(0).getItemId());
            } else {
                log.info("insertItemPrices... Empty response");
            }
            csgoItemMapper.insertItemHistoryPrices(csgoItemHistoryPriceDTOS);
        } catch (Exception e) {
            log.info("insertItemPrices... Err {}", e.getMessage());
        }
    }

    @Override
    public List<CSGOItemHistoryPriceDTO> getItemHistoryPrices(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO) {
       return csgoItemMapper.getItemHistoryPrices(csgoItemHistoryPriceQueryDTO);
    }

    @Override
    public CSGOItemHistoryPriceDTO getItemLatestPrice(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO) {
        return csgoItemMapper.getItemLatestPrice(csgoItemHistoryPriceQueryDTO);
    }

    @Override
    public List<CSGOItemHistoryPriceDTO> getItemHistoryPricesRecent(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO) {
        return csgoItemMapper.getItemHistoryPricesRecent(csgoItemHistoryPriceQueryDTO);
    }

    @Override
    public List<CSGORankingVO> getRanking() {
        return csgoItemMapper.getRanking();
    }

    @Async("taskExecutor")
    public void notifyUser(String userId, String msg) {
        WebSocketServer.sendMessageByUserId(userId, msg);
    }

}
