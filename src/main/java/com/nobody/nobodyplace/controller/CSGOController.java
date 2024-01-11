package com.nobody.nobodyplace.controller;

import com.github.pagehelper.Page;
import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.pojo.dto.*;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryStatusVO;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryVO;
import com.nobody.nobodyplace.response.PageResult;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CSGOItemService;
import com.nobody.nobodyplace.utils.TimeUtil;
import com.nobody.nobodyplace.utils.redis.RedisBit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@EnableAsync
public class CSGOController {
    private static final Logger Nlog = LoggerFactory.getLogger(CSGOController.class);

    private final CSGOItemService csgoItemService;

    final RedisBit redisBit;

    private final RedisTemplate redisTemplate;

    public CSGOController(CSGOItemService csgoItemService, RedisTemplate redisTemplate, RedisBit redisBit) {
        this.csgoItemService = csgoItemService;
        this.redisTemplate = redisTemplate;
        this.redisBit = redisBit;
    }

    private void cleanCache(String pattern) {
        // todo 这是最快的方法吗？
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    @GetMapping(API.GET_ITEM_INFOS)
    @CrossOrigin
    public Result<PageResult> pageQueryItems(CSGOItemPageQueryDTO csgoItemPageQueryDTO){
        Nlog.info("GET_ITEM_INFOS: {}", csgoItemPageQueryDTO);
        // Redis 缓存

        PageResult pageResult = null;
        if (csgoItemPageQueryDTO.getName() == null || csgoItemPageQueryDTO.getName().equals("")) {
            String mainType = csgoItemPageQueryDTO.getMainType() == null ? "" : csgoItemPageQueryDTO.getMainType();
            String exterior = csgoItemPageQueryDTO.getExterior() == null ? "" : csgoItemPageQueryDTO.getExterior();
            int page = csgoItemPageQueryDTO.getPage();
            int pageSize = csgoItemPageQueryDTO.getPageSize();

            String key = "item:exterior" + exterior + ":mainType" + mainType + ":page" + page + ":pageSize" + pageSize;
            PageResult res = (PageResult) redisTemplate.opsForValue().get(key);
            if (res != null && res.getRecords() != null && res.getRecords().size() != 0) {
                Nlog.info("GET_ITEM_INFOS Redis found key = {}", key);
                return Result.success(res);
            }
            pageResult = csgoItemService.pageQuery(csgoItemPageQueryDTO);
            redisTemplate.opsForValue().set(key, pageResult);
        } else {
            pageResult = csgoItemService.pageQuery(csgoItemPageQueryDTO);
        }
        return Result.success(pageResult);
    }

    @PostMapping(API.ADD_USER_ITEM)
    @CrossOrigin
    @ResponseBody
    public Result addUserItem(@RequestBody CSGOAddUserItemDTO csgoAddUserItemDTO) {
        Nlog.info("ADD_USER_ITEM: {}", csgoAddUserItemDTO);
        long id = BaseContext.getCurrentId();
        cleanCache("inventory:user" + id + "*");
        cleanCache("inventory_all:user" + id);
        csgoItemService.addUserItem(csgoAddUserItemDTO);
        return Result.success();
    }

    private PageResult innerGetUserItems(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO) {
        csgoInventoryPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Nlog.info("innerGetUserItems... request: {}", csgoInventoryPageQueryDTO);
        long userId = csgoInventoryPageQueryDTO.getUserId();
        int page = csgoInventoryPageQueryDTO.getPage();
        int pageSize = csgoInventoryPageQueryDTO.getPageSize();
        String key = "inventory:user" + userId + ":page" + page + ":pagesize" + pageSize;
        PageResult res = (PageResult) redisTemplate.opsForValue().get(key);
        if (res != null && res.getRecords() != null) {
            Nlog.info("innerGetUserItems... Redis found key = {}", key);
            return res;
        }
        PageResult pageResult = csgoItemService.getUserInventory(csgoInventoryPageQueryDTO);
        redisTemplate.opsForValue().set(key, pageResult);
        return pageResult;
    }

    private List<CSGOInventoryVO> innerGetUserAllItems() {
        List<CSGOInventoryVO> res;
        long userId = BaseContext.getCurrentId();
        Nlog.info("innerGetUserAllItems... request user: {}", userId);
        String key = "inventory_all:user" + userId;
        res = (List<CSGOInventoryVO>) redisTemplate.opsForValue().get(key);
        if (res != null) {
            Nlog.info("innerGetUserAllItems... Redis found key = {}", key);
            return res;
        }
        // 查所有 item，并且写 redis
        res = csgoItemService.getUserAllInventory(CSGOInventoryPageQueryDTO.builder().userId(userId).build());
        redisTemplate.opsForValue().set(key, res);
        return res;
    }

    @GetMapping(API.GET_USER_ITEMS)
    @CrossOrigin
    @ResponseBody
    public Result<PageResult> getUserItems(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO) {
        return Result.success(innerGetUserItems(csgoInventoryPageQueryDTO));
    }

    /**
     * 这里还要求每一个物品的最新价格
     */
    @GetMapping(API.GET_USER_ITEMS_WITH_UPDATE_PRICE)
    @CrossOrigin
    @ResponseBody
    public Result<PageResult> getUserItemsWithUpdatePrice(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO)
            throws MalformedURLException, ExecutionException, InterruptedException {
        // 1) 拿库存 （redis → DB）
        PageResult pageResult = innerGetUserItems(csgoInventoryPageQueryDTO);
        List<CSGOInventoryVO> records = (List<CSGOInventoryVO>) pageResult.getRecords();
        // 2) 看哪个物品最新的价格没拿到
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int dOfYear = now.getDayOfYear();
        String webRequestKey = "pricefetch:item%s:year" + year;
        List<CSGOItemHistoryPriceQueryDTO> requestList = new ArrayList<>();
        for (CSGOInventoryVO record : records) {
            Long itemId = record.getItemId();
            String key = String.format(webRequestKey, itemId);

            if (redisBit.get(key) == null || !redisBit.getBit(key, dOfYear)) {
                // 没有更新的话，放到请求队列
                requestList.add(CSGOItemHistoryPriceQueryDTO.builder()
                        .itemId(itemId)
                        .from(TimeUtil.localDateTimeToStr(now.minusDays(30), TimeUtil.NORMAL_FORMAT_PATTERN))
                        .to(TimeUtil.localDateTimeToStr(now, TimeUtil.NORMAL_FORMAT_PATTERN))
                        .build());
            }
        }
        // 3）批量请求这些物品
        for (CSGOItemHistoryPriceQueryDTO request : requestList) {
            String key = String.format(webRequestKey, request.getItemId());
            requestAndSaveItemPrice(request, key, dOfYear);
        }
        // 4）更新
        for (CSGOInventoryVO item : records) {
            Float latestPrice = getItemLatestPrice(item.getItemId());
            item.setCurrentPrice(latestPrice);
        }
        pageResult.setRecords(records);
        return Result.success(pageResult);
    }


    @PostMapping(API.DELETE_USER_ITEM)
    @CrossOrigin
    @ResponseBody
    public Result deleteUserItem(@RequestBody CSGODeleteInventoryDTO csgoDeleteInventoryDTO) {
        long userId = BaseContext.getCurrentId();
        csgoDeleteInventoryDTO.setUserId(userId);
        Nlog.info("DELETE_USER_ITEM: {}", csgoDeleteInventoryDTO);
        cleanCache("inventory:user" + userId + "*");
        cleanCache("inventory_all:user" + userId);
        csgoItemService.deleteUserInventory(csgoDeleteInventoryDTO);
        return Result.success();
    }

    private List<CSGOItemHistoryPriceDTO> requestAndSaveItemPrice(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO,
                                                                  String webRequestKey, int dOfYear) throws ExecutionException, InterruptedException, MalformedURLException {
        Nlog.info("getItemHistoryPrice... web request for {}", csgoItemHistoryPriceQueryDTO);
        Future<List<CSGOItemHistoryPriceDTO>> future = csgoItemService.requestItemPrices(csgoItemHistoryPriceQueryDTO);
        List<CSGOItemHistoryPriceDTO> ret = future.get();  // fixme 有问题的，马上去拿，请求完了统一去拿应该
        Nlog.info("getItemHistoryPrice... writing db for result {}", csgoItemHistoryPriceQueryDTO);
        csgoItemService.insertItemPrices(ret);
        // 然后标记为已查询
        redisBit.setBit(webRequestKey, dOfYear, true);
        return ret;
    }

    /**
     * 获取某个物品的当天的最新价格
     * 网络 -> 持久化 -> redis
     * @param itemId
     * @return
     * @throws MalformedURLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private Float getItemLatestPrice(Long itemId) throws MalformedURLException, ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int dOfYear = now.getDayOfYear();
        String webRequestKey = "pricefetch:item" + itemId + ":year" + year;
        CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO = CSGOItemHistoryPriceQueryDTO.builder().itemId(itemId).build();
        csgoItemHistoryPriceQueryDTO.setFrom(TimeUtil.localDateTimeToStr(now.minusDays(30), TimeUtil.NORMAL_FORMAT_PATTERN));
        csgoItemHistoryPriceQueryDTO.setTo(TimeUtil.localDateTimeToStr(now, TimeUtil.NORMAL_FORMAT_PATTERN));
        if (redisBit.get(webRequestKey) == null || !redisBit.getBit(webRequestKey, dOfYear)) {
            List<CSGOItemHistoryPriceDTO> res = requestAndSaveItemPrice(csgoItemHistoryPriceQueryDTO, webRequestKey, dOfYear);
            if (res == null || res.size() == 0) {
                Nlog.info("getItemLatestPrice... err, no ele for request {}", csgoItemHistoryPriceQueryDTO);
                return (float) -1;
            }
        }
        // 看看 内存有没有
        String dataKey = "pricehistory:item" + itemId + ":time" + year + "_" + dOfYear;
        List<CSGOItemHistoryPriceDTO> res = (List<CSGOItemHistoryPriceDTO>) redisTemplate.opsForValue().get(dataKey);
        if (res == null) {
            res = csgoItemService.getItemHistoryPrices(csgoItemHistoryPriceQueryDTO);
            if (res == null || res.size() == 0) {
                Nlog.info("getItemLatestPrice... err, no data found in DB for request {}", csgoItemHistoryPriceQueryDTO);
                return (float) -1;
            }
            redisTemplate.opsForValue().set(dataKey, res);
            return res.get(0).getPrice();
        }
        return res.get(0).getPrice();
//        return csgoItemService.getItemLatestPrice(CSGOItemHistoryPriceQueryDTO.builder().itemId(itemId).build()).getPrice();
    }

    @PostMapping(API.GET_ITEM_PRICE)
    @CrossOrigin
    @ResponseBody
    public Result getItemHistoryPrice(@RequestBody CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO)
            throws MalformedURLException, InterruptedException, ExecutionException {
        List<CSGOItemHistoryPriceDTO> ret;
        // 1. 看这个 item 的 bitmap，看看今天有没有更新【指的是走网络查真正的】
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int dOfYear = now.getDayOfYear();

        // 一律查询 30 天内的
        // 后续增加会员逻辑的话可以判断，并展示不同的范围
        csgoItemHistoryPriceQueryDTO.setFrom(TimeUtil.localDateTimeToStr(now.minusDays(30), TimeUtil.NORMAL_FORMAT_PATTERN));
        csgoItemHistoryPriceQueryDTO.setTo(TimeUtil.localDateTimeToStr(now, TimeUtil.NORMAL_FORMAT_PATTERN));

        String webRequestKey = "pricefetch:item" + csgoItemHistoryPriceQueryDTO.getItemId() + ":year" + year;

        if (redisBit.get(webRequestKey) == null || !redisBit.getBit(webRequestKey, dOfYear)) {
            // 2. 没有更新的话，走网络请求，然后持久化
            ret = requestAndSaveItemPrice(csgoItemHistoryPriceQueryDTO, webRequestKey, dOfYear);
        } else {
            Nlog.info("getItemHistoryPrice... Requested before for {}", csgoItemHistoryPriceQueryDTO);
        }
        // 上面的 ret 不去信任，因为不符合请求的 from 和 to
        // 然后这里统一去请求，拿数据，
        // 1.1 更新了的话，看 redis，数据有没有在内存里面？
        String dataKey = "pricehistory:item" + csgoItemHistoryPriceQueryDTO.getItemId() + ":time" + year + "_" + dOfYear;
        // 这里这样子设计，因为首先，一天的价格不会变，所以这一天当天的请求都可以缓存，
        // 然后到第二天，前面的都可以不用了，然后可以做删除操作
        ret = (List<CSGOItemHistoryPriceDTO>) redisTemplate.opsForValue().get(dataKey);
        if (ret == null) {
            // 1.2 redis 没有的话，查 DB，然后写内存
            ret = csgoItemService.getItemHistoryPrices(csgoItemHistoryPriceQueryDTO);
            redisTemplate.opsForValue().set(dataKey, ret);
        }
        return Result.success(ret);
    }

    /**
     * 获取总价格 + 市场总当前价格 + offset
     * @return
     */
    @GetMapping(API.GET_INVENTORY_STATUS)
    @CrossOrigin
    @ResponseBody
    public Result getInventoryStatus() throws MalformedURLException, ExecutionException, InterruptedException {
        // 先拿当前用户的所有库存
        List<CSGOInventoryVO> userInventory = innerGetUserAllItems();
        double totalSpent = 0;
        double totalCurrent = 0;
        // 查询所有的最新价格
        for (CSGOInventoryVO inv : userInventory) {
            totalSpent += inv.getBoughtPrice();
            totalCurrent += getItemLatestPrice(inv.getItemId());
        }
        DecimalFormat df = new DecimalFormat("#.00");
        CSGOInventoryStatusVO ret = CSGOInventoryStatusVO.builder()
                .spentTotalPrice(df.format(totalSpent))
                .currentTotalPrice(df.format(totalCurrent))
                .gain(df.format(totalCurrent - totalSpent))
                .itemCount(userInventory.size())
                .build();
        return Result.success(ret);
    }

}
