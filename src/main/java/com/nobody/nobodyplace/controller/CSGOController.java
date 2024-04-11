package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.pojo.dto.*;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryStatusVO;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryVO;
import com.nobody.nobodyplace.pojo.vo.CSGORankingVO;
import com.nobody.nobodyplace.response.PageResult;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CSGOItemService;
import com.nobody.nobodyplace.service.WebSocketServer;
import com.nobody.nobodyplace.utils.TimeUtil;
import com.nobody.nobodyplace.utils.redis.RedisBit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@EnableAsync
public class CSGOController {
    private static final Logger Nlog = LoggerFactory.getLogger(CSGOController.class);

    private final CSGOItemService csgoItemService;

    final RedisBit redisBit;

    private final RedisTemplate redisTemplate;

    // redis keys
    private final static String ITEM_PAGE_QUERY_KEY = "item:exterior%s:mainType%s:page%d:pageSize%d";
    private final static String USER_INV_PAGE_QUERY_PREFIX = "inventory:user%d*";
    private final static String USER_INV_PAGE_QUERY_KEY = "inventory:user%d:page%d:pagesize%d";
    private final static String USER_ALL_INV_QUERY_KEY = "inventory_all:user%d";
    private final static String WEB_REQUEST_KEY = "pricefetch:item%d:year%d";
    private final static String ITEM_PRICE_HISTORY_KEY = "pricehistory:item%d:time%d_%d";
    private final static String RANKING_KEY = "csgo_ranking";
    private final static String USER_INFO_KEY = "userinfo:user%d";

    private final static String CRON_TRANSACTION = "0 * * * * ?";  // for testing , real is "0 0 16 * * ?"


//    private static ConcurrentHashMap<Long, String> userIdToNameMap = new ConcurrentHashMap<>();

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
            String key = String.format(ITEM_PAGE_QUERY_KEY, exterior, mainType, page, pageSize);
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

    private void updateRanking(Long id, Float boughtPrice) {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Double score = zSetOperations.score(RANKING_KEY, id);
        if (score == null) {
            // 初始化
            getRanking();
        } else {
            zSetOperations.incrementScore(RANKING_KEY, id, boughtPrice);
        }
    }

    @PostMapping(API.ADD_USER_ITEM)
    @CrossOrigin
    @ResponseBody
    public Result addUserItem(@RequestBody CSGOAddUserItemDTO csgoAddUserItemDTO) {
        Nlog.info("ADD_USER_ITEM: {}", csgoAddUserItemDTO);
        long id = BaseContext.getCurrentId();
        cleanCache(String.format(USER_INV_PAGE_QUERY_PREFIX, id));
        cleanCache(String.format(USER_ALL_INV_QUERY_KEY, id));
        csgoItemService.addUserItem(csgoAddUserItemDTO);
        updateRanking(id, csgoAddUserItemDTO.getBoughtPrice());
        return Result.success();
    }

    private PageResult innerGetUserItems(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO) {
        csgoInventoryPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Nlog.info("innerGetUserItems... request: {}", csgoInventoryPageQueryDTO);
        long userId = csgoInventoryPageQueryDTO.getUserId();
        int page = csgoInventoryPageQueryDTO.getPage();
        int pageSize = csgoInventoryPageQueryDTO.getPageSize();
        String key = String.format(USER_INV_PAGE_QUERY_KEY, userId, page, pageSize);
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
        String key = String.format(USER_ALL_INV_QUERY_KEY, userId);
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
        List<CSGOItemHistoryPriceQueryDTO> requestList = userInventoryToRequestList(records);
        // 3）批量请求这些物品
        if (!requestList.isEmpty()) {
            batchRequestAndSaveItemPrice(requestList);
        }
        // 4）更新
        for (CSGOInventoryVO item : records) {
            Long itemId = item.getItemId();
            Float latestPrice = getItemLatestPrice(itemId);
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
        cleanCache(String.format(USER_INV_PAGE_QUERY_PREFIX, userId));
        cleanCache(String.format(USER_ALL_INV_QUERY_KEY, userId));
        Float boughtPrice = csgoItemService.deleteUserInventory(csgoDeleteInventoryDTO);
        updateRanking(userId, -boughtPrice);
        return Result.success();
    }

    private List<CSGOItemHistoryPriceDTO> requestAndSaveItemPrice(CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO,
                                                                  String webRequestKey, int dOfYear) throws ExecutionException, InterruptedException, MalformedURLException {
        Nlog.info("getItemHistoryPrice... web request for {}", csgoItemHistoryPriceQueryDTO);
        Future<List<CSGOItemHistoryPriceDTO>> future = csgoItemService.requestItemPrices(csgoItemHistoryPriceQueryDTO);
        List<CSGOItemHistoryPriceDTO> ret = future.get();  // fixme 有问题的，马上去拿，请求完了统一去拿应该
        // fixed：在  batchRequestAndSaveItemPrice 中完成上上述问题的修改
        Nlog.info("getItemHistoryPrice... writing db for result {}", csgoItemHistoryPriceQueryDTO);
        csgoItemService.insertItemPrices(ret);
        // 然后标记为已查询
        redisBit.setBit(webRequestKey, dOfYear, true);
        return ret;
    }

    private Set<Long> batchRequestAndSaveItemPrice(List<CSGOItemHistoryPriceQueryDTO> requests) throws ExecutionException, InterruptedException, MalformedURLException {
        Nlog.info("batchRequestAndSaveItemPrice... web request for {}", requests);
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int dOfYear = now.getDayOfYear();
        String rawKey = "pricefetch:item%s:year" + year;
        Map<CSGOItemHistoryPriceQueryDTO, Future<List<CSGOItemHistoryPriceDTO>>> resultMap = new HashMap<>();
        Set<Long> successResponse = new HashSet<>();
        // 请求的时候可能有重复的，不要重复查询
        Set<Long> requestIds = new HashSet<>();
        for (CSGOItemHistoryPriceQueryDTO request : requests) {
            // 先批量请求
            String key = String.format(rawKey, request.getItemId());
            if (redisBit.get(key) != null && redisBit.getBit(key, dOfYear)) {
                Nlog.info("batchRequestAndSaveItemPrice... No need to request {}", request);
                successResponse.add(request.getItemId());
            } else {
//                Nlog.info("batchRequestAndSaveItemPrice... Requesting {}", request);
                if (!requestIds.contains(request.getItemId())) {
                    // 请求的时候可能有重复的，不要重复查询
                    Future<List<CSGOItemHistoryPriceDTO>> future = csgoItemService.requestItemPrices(request);
                    resultMap.put(request, future);
                    requestIds.add(request.getItemId());
                }
            }
        }

        for (CSGOItemHistoryPriceQueryDTO request : requests) {
            // 再处理回包
            String key = String.format(rawKey, request.getItemId());
            Future<List<CSGOItemHistoryPriceDTO>> future = resultMap.getOrDefault(request, null);
            if (future != null) {
                List<CSGOItemHistoryPriceDTO> ret = future.get();
                if (ret != null && ret.size() > 0) {
                    csgoItemService.insertItemPrices(ret);
                    redisBit.setBit(key, dOfYear, true);
                    successResponse.add(request.getItemId());
                } else {
                    Nlog.info("batchRequestAndSaveItemPrice... request for {} filed to response", request.getItemId());
                }
            }
        }
        return successResponse;
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
        String webRequestKey = String.format(WEB_REQUEST_KEY, itemId, year);
        CSGOItemHistoryPriceQueryDTO csgoItemHistoryPriceQueryDTO = CSGOItemHistoryPriceQueryDTO.builder().itemId(itemId).build();
        csgoItemHistoryPriceQueryDTO.setFrom(TimeUtil.localDateTimeToStr(now.minusDays(30), TimeUtil.NORMAL_FORMAT_PATTERN));
        csgoItemHistoryPriceQueryDTO.setTo(TimeUtil.localDateTimeToStr(now, TimeUtil.NORMAL_FORMAT_PATTERN));
        if (redisBit.get(webRequestKey) == null || !redisBit.getBit(webRequestKey, dOfYear)) {
            List<CSGOItemHistoryPriceDTO> res = requestAndSaveItemPrice(csgoItemHistoryPriceQueryDTO, webRequestKey, dOfYear);
            if (res == null || res.size() == 0) {
                Nlog.info("getItemLatestPrice... err, no ele for request {}", csgoItemHistoryPriceQueryDTO);
                return (float) 0;
            }
        }
        // 看看 内存有没有
        String dataKey = String.format(ITEM_PRICE_HISTORY_KEY, itemId, year, dOfYear);
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
        String webRequestKey = String.format(WEB_REQUEST_KEY, csgoItemHistoryPriceQueryDTO.getItemId(), year);

        if (redisBit.get(webRequestKey) == null || !redisBit.getBit(webRequestKey, dOfYear)) {
            // 2. 没有更新的话，走网络请求，然后持久化
            ret = requestAndSaveItemPrice(csgoItemHistoryPriceQueryDTO, webRequestKey, dOfYear);
        } else {
            Nlog.info("getItemHistoryPrice... Requested before for {}", csgoItemHistoryPriceQueryDTO);
        }
        // 上面的 ret 不去信任，因为不符合请求的 from 和 to
        // 然后这里统一去请求，拿数据，
        // 1.1 更新了的话，看 redis，数据有没有在内存里面？
        String dataKey = String.format(ITEM_PRICE_HISTORY_KEY, csgoItemHistoryPriceQueryDTO.getItemId(), year, dOfYear);
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

        List<CSGOItemHistoryPriceQueryDTO> requestList = userInventoryToRequestList(userInventory);
        if (!requestList.isEmpty()) {
            Thread.sleep(10000);
            batchRequestAndSaveItemPrice(requestList);
        }
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

    private List<CSGOItemHistoryPriceQueryDTO> userInventoryToRequestList(List<CSGOInventoryVO> records) {
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
        return requestList;
    }

    @GetMapping(API.GET_RANKING)
    @CrossOrigin
    @ResponseBody
    public Result getRanking() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set<Long> s = zSetOperations.reverseRange(RANKING_KEY, 0, -1);
        List<CSGORankingVO> ret = new ArrayList<>();
        if (s != null && s.size() > 0) {
            int cur = 0;
            for (Long userId : s) {
                if (cur >= 10) {
                    break;
                }
                ret.add(CSGORankingVO.builder()
                        .userId(userId)
                        .userName((String) redisTemplate.opsForValue().get(String.format(USER_INFO_KEY, userId)))
                        .ownPrice(zSetOperations.score(RANKING_KEY, userId))
                        .build());
                cur++;
            }
            Long rank = zSetOperations.reverseRank(RANKING_KEY, BaseContext.getCurrentId()) + 1;
            return Result.success(ret, String.valueOf(rank));
        }
        ret = csgoItemService.getRanking();
        for (CSGORankingVO ranking : ret) {
            zSetOperations.add(RANKING_KEY, ranking.getUserId(), ranking.getOwnPrice());
            redisTemplate.opsForValue().set(String.format(USER_INFO_KEY, ranking.getUserId()), ranking.getUserName());
        }
        int to = Math.min(ret.size(), 10);
        Long rank = zSetOperations.reverseRank(RANKING_KEY, BaseContext.getCurrentId()) + 1;
        return Result.success(ret.subList(0, to), String.valueOf(rank));
    }

    @GetMapping("api/send")
    @CrossOrigin
    @ResponseBody
    public void testWsSend(){
        WebSocketServer.sendMessageByUserId(String.valueOf(BaseContext.getCurrentId()), "okokokokok");
    }

    @Scheduled(cron = CRON_TRANSACTION)  // "秒域 分域 时域 日域 月域 周域 年域"
    public void notifyTransaction() {
        for (Map.Entry<String, Set<String>> connEntry : WebSocketServer.connection.entrySet()) {
            csgoItemService.notifyUser(connEntry.getKey(), "市场当日交易时间已到，请关注最新商品动态！");
        }
    }

}
