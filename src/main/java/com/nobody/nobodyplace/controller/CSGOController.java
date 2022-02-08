package com.nobody.nobodyplace.controller;

import com.google.gson.Gson;
import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.gson.csgo.MarketHistoryItemInfoResponse;
import com.nobody.nobodyplace.requestbody.RequestAddUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestGetIncomeAddup;
import com.nobody.nobodyplace.requestbody.RequestGetUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestItemHistoryPrice;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.response.csgo.PriceHistoryData;
import com.nobody.nobodyplace.response.csgo.UserTransactionData;
import com.nobody.nobodyplace.service.CommonStorageService;
import com.nobody.nobodyplace.service.CsgoService;
import com.nobody.nobodyplace.utils.HttpUtil;
import com.nobody.nobodyplace.utils.TimeUtil;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Controller
public class CSGOController {
    private static final Logger Nlog = LoggerFactory.getLogger(CSGOController.class);

    private static final String API_GET_ITEM_ID = "https://buff.163.com/api/market/search/suggest?game=csgo";
    private static final String API_GET_ITEM_PAST_7_DAY_TRANSACTION = "https://buff.163.com/api/market/goods/price_history/buff?game=csgo&currency=CNY&days=7";
    private static final String API_GET_ITEM_PAST_MONTH_TRANSACTION = "https://buff.163.com/api/market/goods/price_history/buff?game=csgo&currency=CNY&days=30";
    private static final String API_GET_ITEM_PAST_6_MONTH_TRANSACTION = "https://buff.163.com/api/market/goods/price_history/buff?game=csgo&currency=CNY&days=180";
    private static final String API_GET_ITEM_RECENT_TRANSACTION = "https://buff.163.com/api/market/goods/bill_order?game=csgo";

    final CsgoService service;
    final CommonStorageService cookieService;

    private String cookie;
    private long cookieVerifyTime;

    private final Object requestLock = new Object(); // 同时只能存在一个请求

    /**
     * itemId 和实体类的映射缓存
     */
    private final ConcurrentHashMap<Integer, CsgoItem> itemCache;



    /**
     * 历史价格一天只刷新一次
     * k: item_id v: last updated time
     */
    private final ConcurrentHashMap<Integer, Long> historyPriceUpdateMap;

    public CSGOController(CsgoService service, CommonStorageService cookieService) {
        this.service = service;
        this.cookieService = cookieService;
        this.itemCache = new ConcurrentHashMap<>();
        this.historyPriceUpdateMap = new ConcurrentHashMap<>();
    }

    /**
     * cookie 不存在或者上个 cookie 失效了，就去提醒去更新。
     * fixme 看了两小时，并没有从 chrome 中取 cookie 的办法，很好奇 postman 是怎么做到的，目前只能手动粘
     */
    private boolean hasValidCookie() {
        if (System.currentTimeMillis() - cookieVerifyTime < TimeUtil.HOUR) {
            return true;
        }
        if (TextUtils.isEmpty(cookie)) {
            cookie = cookieService.getBuffCookie();
            if (TextUtils.isEmpty(cookie)) {
                return false;
            }
        }
        HttpUtil.HttpResponse response;
        try {
            // 由于搜索 api 可以频繁去调，通过搜索 api 检测 cookie 是否有效
            response = HttpUtil.get(new URL(API_GET_ITEM_ID), false, 2000, cookie);
        } catch (MalformedURLException e) {
            // won't happen...
            cookie = "";
            return false;
        }
        if (response.code == 200) {
            cookieVerifyTime = System.currentTimeMillis();
            return true;
        }
        // false if code == 302
        return false;
    }

    @CrossOrigin
    @ResponseBody
    @PostMapping(value = API.BATCH_GET_ITEM_DETAIL)
    public Result batchGetItemDetail() {
        return new Result(0);
    }

    /**
     * 批量获取 itemId 对应的商品详情。
     * 调用该接口，说明 itemId 已经存在于 db，不然拿不到 itemId
     * @param requestList [itemId0, itemId1, ...]
     * @return 商品详情列表
     */
    private List<CsgoItem> innerBatchGetItemDetail(List<Integer> requestList) {
        List<CsgoItem> ret = new ArrayList<>();
        List<Integer> DBSearchRequests = new ArrayList<>();
        for (int itemId : requestList) {
            if (itemCache.containsKey(itemId)) {
                ret.add(itemCache.get(itemId));
            } else {
                DBSearchRequests.add(itemId);
            }
        }
        List<CsgoItem> searchResult = service.batchGetItemInfo(DBSearchRequests);
        for (CsgoItem item : searchResult) {
            itemCache.put(item.getItemId(), item);
            ret.add(item);
        }
        return ret;
    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(value = API.GET_USER_PROPERTY)
    public Result getUserProperty() {
        return new Result(0);
    }

    /**
     * 获取 item 对应的历史记录价格<br/>
     * 数据量：一个商品一个月大概200多条
     * @param requestItems [{id: id, from: 开始时间戳, to: 结束时间戳}]
     * @return Result data: {@link PriceHistoryData}
     */
    @CrossOrigin
    @ResponseBody
    @PostMapping(value = API.BATCH_GET_ITEM_HISTORY_PRICE)
    public Result batchGetItemHistoryPrice(@RequestBody List<RequestItemHistoryPrice> requestItems) {
        if (!hasValidCookie()) {
            return new Result(500, "Login required");
        }
        long delayTime = 1000;
        DelayQueue<DelayElement> requestQueue = new DelayQueue<>();
        for (RequestItemHistoryPrice request : requestItems) {
            if (historyPriceUpdateMap.containsKey(request.id)) {
                if (TimeUtil.isToday(historyPriceUpdateMap.get(request.id))) {
                    Nlog.info("batchGetItemHistoryPrice... No need to request api for itemId = " + request.id);
                    continue;
                }
            }
            Nlog.info("batchGetItemHistoryPrice.. Adding itemId " + request.id + " to request list");
            requestQueue.put(new DelayElement(delayTime, request.id));
            delayTime += 1000;
        }
        synchronized (requestLock) {
            while (!requestQueue.isEmpty()){
                int itemId = 0;
                try {
                    itemId = requestQueue.take().itemId;
                    // todo 根据上次查询时间确定 7 天和或 30 天的粒度
                    String requestUrl = HttpUtil.setUrlParam(API_GET_ITEM_PAST_7_DAY_TRANSACTION, "goods_id", itemId);
                    HttpUtil.HttpResponse response = HttpUtil.get(new URL(requestUrl), false, 10000, cookie);
                    MarketHistoryItemInfoResponse historyItemInfoResponse =
                            new Gson().fromJson(response.data, MarketHistoryItemInfoResponse.class);
                    List<List<Float>> histories = historyItemInfoResponse.getPriceHistory();
                    if (histories != null && !histories.isEmpty()) {
                        service.batchAddPriceHistory(itemId, histories);
                        // 更新时间
                        historyPriceUpdateMap.put(itemId, System.currentTimeMillis());
                        Nlog.info("batchGetItemHistoryPrice... Successfully handled request for itemId = " + itemId);
                    } else {
                        Nlog.info("batchGetItemHistoryPrice... History is empty for itemId = " + itemId);
                    }
                } catch (InterruptedException | MalformedURLException e) {
                    Nlog.info("batchGetItemHistoryPrice... Exception Occurs while requesting itemId = " + itemId + ", err: " + e.toString());
                    return new Result(404);
                }
            }
        }
        // response
        // [{id: id, price: [[time0, price0], [time1, price1], ...]}, ...]
        // 其实我觉得历史价格请求，前端过来的时候应该是一个个的，因为它展示数据也是一个个去展示的
        // 但是可以说第一次打开网页的时候统一全部更新，避免漏了一些没记录的
        // 让我想到在腾讯做的一个需求，明明标签图标只有一个，愣是要扩展做成多个的，说是为了可扩展，无语子
        // 这个接口先写成批量的 01/29/2022
        Result result = new Result(0);
        result.data = new PriceHistoryData();
        ((PriceHistoryData) (result.data)).infos = service.batchGetPriceHistories(requestItems);
        return result;
    }

    /**
     * 添加用户交易记录
     * @param request {@link RequestAddUserTransaction}
     * @return 基本状态
     */
    @CrossOrigin
    @ResponseBody
    @PostMapping(value = API.ADD_USER_TRANSACTION)
    public Result addUserTransaction(@RequestBody RequestAddUserTransaction request) {
        // 默认请求有效
        try {
            service.addTransaction(request);
            Nlog.info("addUserTransaction... Successfully added user transaction: " + request);
            return new Result(0);
        } catch (Exception e) {
            Nlog.info("addUserTransaction... failed, err = " + e.toString());
        }
        return new Result(404);
    }

    /**
     * 查询用户交易记录
     * @param request {@link RequestGetUserTransaction}
     * @return 见 API 文档
     */
    @CrossOrigin
    @ResponseBody
    @PostMapping(value = API.GET_USER_TRANSACTION)
    public Result getUserTransaction(@RequestBody RequestGetUserTransaction request) {
        if (request.from > request.to) {
            return new Result(400, "Invalid request body");
        }
        try {
            List<CsgoUserTransaction> transactions = service.getTransaction(request);
            Result result = new Result(0);
            result.data = new UserTransactionData(transactions);
            return result;
        } catch (Exception e) {
            return new Result(400);
        }
    }

    /**
     * 延时队列元素
     */
    static class DelayElement implements Delayed {

        // 延迟截止时间
        long delayTime = System.currentTimeMillis();

        // 商品 id
        int itemId;

        public DelayElement(long delayTime) {
            this.delayTime = (this.delayTime + delayTime);
        }

        public DelayElement(long delayTime, int id) {
            this.delayTime = (this.delayTime + delayTime);
            this.itemId = id;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }
    }
}
