package com.nobody.nobodyplace.controller;

import com.google.gson.Gson;
import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistory;
import com.nobody.nobodyplace.entity.csgo.CsgoUserProperty;
import com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.gson.csgo.LeaseRecordResponse;
import com.nobody.nobodyplace.gson.csgo.MarketHistoryItemInfoResponse;
import com.nobody.nobodyplace.requestbody.RequestGetIncomeStatus;
import com.nobody.nobodyplace.requestbody.RequestGetUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestItemHistoryPrice;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.response.csgo.IncomeStatusData;
import com.nobody.nobodyplace.response.csgo.PriceHistoryData;
import com.nobody.nobodyplace.response.csgo.UserPropertyData;
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
import java.util.*;
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
    private static final String API_GET_LEASE_RECORDS = "https://api.youpin898.com/api/v2/commodity/Lease/GetLeaseRecordList?RoleType=1&pageSize=20&pageIndex=1&gameId=730&status=600";

    final CsgoService service;
    final CommonStorageService commonService;

    private String buffCookie;
    private String yoyoCookie;
    private long buffCookieVerifyTime;

    private long leaseRecordUpdateTime;

    private static final Object requestLock = new Object(); // 同时只能存在一个请求

    /**
     * itemId 和实体类的映射缓存
     */
    private final ConcurrentHashMap<Integer, CsgoItem> itemCache;

    /**
     * item 全称和实体类的映射缓存 <br/>
     * 由于使用悠悠api获取租赁信息的时候，只能通过全称唯一定位商品，故只能采取该下策，item 存了两遍
     */
    private final ConcurrentHashMap<String, CsgoItem> itemDescCache;


    /**
     * 历史价格一天只刷新一次
     * k: item_id v: last updated time
     */
    private final ConcurrentHashMap<Integer, Long> historyPriceUpdateMap;

    public CSGOController(CsgoService service, CommonStorageService commonService) {
        this.service = service;
        this.commonService = commonService;
        this.itemCache = new ConcurrentHashMap<>();
        this.itemDescCache = new ConcurrentHashMap<>();
        this.historyPriceUpdateMap = new ConcurrentHashMap<>();
    }

    /**
     * cookie 不存在或者上个 cookie 失效了，就去提醒去更新。
     * fixme 看了两小时，并没有从 chrome 中取 cookie 的办法，很好奇 postman 是怎么做到的，目前只能手动粘
     */
    private boolean hasValidBuffCookie() {
        if (System.currentTimeMillis() - buffCookieVerifyTime < TimeUtil.MILLI_HOUR) {
            return true;
        }
        if (TextUtils.isEmpty(buffCookie)) {
            buffCookie = commonService.getBuffCookie();
            if (TextUtils.isEmpty(buffCookie)) {
                return false;
            }
        }
        HttpUtil.HttpResponse response;
        try {
            // 由于搜索 api 可以频繁去调，通过搜索 api 检测 cookie 是否有效
            response = HttpUtil.get(new URL(API_GET_ITEM_ID), false, 2000, buffCookie);
        } catch (MalformedURLException e) {
            // won't happen...
            return false;
        }
        if (response.code == 200) {
            buffCookieVerifyTime = System.currentTimeMillis();
            return true;
        }
        // false if code == 302
        return false;
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
            itemDescCache.put(item.getDesc(), item);
            ret.add(item);
        }
        return ret;
    }

    /**
     * 将 DB 里面所有商品基本信息存入两个 cache
     */
    private void fetchAllItemDetails() {
        List<CsgoItem> ret = service.getAllItemInfo();
        for (CsgoItem item : ret) {
            this.itemCache.put(item.getItemId(), item);
            this.itemDescCache.put(item.getDesc(), item);
        }
    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(value = API.GET_USER_PROPERTY)
    public Result getUserProperty() {
        if (itemCache.isEmpty()) {
            fetchAllItemDetails();
        }
        List<CsgoUserProperty> res = service.getUserProperty();
        Result result = new Result(0);
        result.data = new UserPropertyData();
        for (CsgoUserProperty property : res) {
            if (itemCache.containsKey(property.getItemId())) {
                ((UserPropertyData) (result.data)).properties.add(itemCache.get(property.getItemId()));
            } else {
                Nlog.info("getUserProperty... itemCache doesn't contain item id = " + property.getItemId());
            }
        }
        return result;
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
        if (!hasValidBuffCookie()) {
            buffCookie = "";
            Nlog.info("batchGetItemHistoryPrice... require BUFF login");
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
        if (!requestQueue.isEmpty()) {
            synchronized (requestLock) {
                while (!requestQueue.isEmpty()){
                    int itemId = 0;
                    try {
                        itemId = requestQueue.take().itemId;
                        // todo 根据上次查询时间确定 7 天和或 30 天的粒度
                        String requestUrl = HttpUtil.setUrlParam(API_GET_ITEM_PAST_7_DAY_TRANSACTION, "goods_id", itemId);
                        HttpUtil.HttpResponse response = HttpUtil.get(new URL(requestUrl), false, 10000, buffCookie);
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
                        return new Result(-1);
                    }
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
        result.data = new PriceHistoryData(service.batchGetPriceHistories(requestItems));
        return result;
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
            return new Result(-1, "Invalid request body");
        }
        // 一天自动更新一次，除非走强制更新
        if (System.currentTimeMillis() - leaseRecordUpdateTime > TimeUtil.MILLI_DAY || request.fetch == 1) {
            if (!getLeaseRecords()) {
                Nlog.info("getUserTransaction... Can not get lease records");
                return new Result(-1, "Can not get lease records. Check internet or cookie.");
            }
            leaseRecordUpdateTime = System.currentTimeMillis();
        }

        try {
            List<CsgoUserTransaction> transactions = service.getTransaction(request);
            Result result = new Result(0);
            result.data = new UserTransactionData(transactions);
            return result;
        } catch (Exception e) {
            Nlog.info("getUserTransaction... ");
            return new Result(-1);
        }
    }

    /**
     * 获取悠悠前 20 条租赁「完成」的订单信息，并存入 DB
     * @return 成果与否
     */
    private boolean getLeaseRecords() {
        // 由于在悠悠中只能通过desc唯一确定商品是什么，故要确保数据已经加载到缓存了
        if (itemDescCache.isEmpty()) {
            // 目前的业务没有说可以动态添加商品信息的，所以读一次缓存就行了，后续如果可以动态添加商品，则需要修改这里
            fetchAllItemDetails();
        }

        yoyoCookie = commonService.getYoyoCookie();
        if (TextUtils.isEmpty(yoyoCookie)) {
            Nlog.info("getLeaseRecords... cookie is empty");
            return false;
        }

        try {
            HttpUtil.HttpResponse response = HttpUtil.get(new URL(API_GET_LEASE_RECORDS), false,
                    10000, "", new String[]{"authorization", yoyoCookie});
            LeaseRecordResponse records = new Gson().fromJson(response.data, LeaseRecordResponse.class);
            if (records.Code != 0) {
                // 可能是 cookie 过期了，前端弹框
                Nlog.info("getLeaseRecords... cookie is expired or no connection to internet");
                return false;
            }
            List<CsgoUserTransaction> tmpArr = new ArrayList<>();
            for (LeaseRecordResponse.LeaseRecord record : records.Data) {
                if (itemDescCache.containsKey(record.Name)) {
                    CsgoItem item = itemDescCache.get(record.Name);
                    tmpArr.add(new CsgoUserTransaction(item.getItemId(), record.OrderNo, TimeUtil.toTimeStampSeconds(record.ReturnTime), record.LeaseUnitPrice, (byte) 0, record.LeaseDays));
                } else {
                    Nlog.info("getLeaseRecords... cache doesn't contain desc = " + record.Name);
                }
            }
            service.addTransaction(tmpArr);
        } catch (Exception e) {
            Nlog.info("getLeaseRecords... exception: " + e.toString());
            return false;
        }
        return true;
    }

    @CrossOrigin
    @ResponseBody
    @PostMapping(value = API.GET_INCOME_STATUS)
    public Result getIncomeStatus(@RequestBody RequestGetIncomeStatus request) {
        if (request.type == RequestGetIncomeStatus.TYPE_LEASE) {
            return getLeaseIncomeStatus(request);
        } else if (request.type == RequestGetIncomeStatus.TYPE_SELL) {
            Nlog.info("getIncomeStatus... TYPE_SELL unsupported");
        } else if (request.type == RequestGetIncomeStatus.TYPE_HOLDING) {
            return getHoldingIncomeStatus(request);
        }
        Nlog.info("getIncomeStatus... Unknown income type");
        return new Result(-1, "unknown income type");
    }

    private Result getLeaseIncomeStatus(RequestGetIncomeStatus request) {
        if (System.currentTimeMillis() - leaseRecordUpdateTime > TimeUtil.MILLI_DAY || request.fetch == 1) {
            if (!getLeaseRecords()) {
                return new Result(-1);
            }
            leaseRecordUpdateTime = System.currentTimeMillis();
        }

        List<CsgoUserTransaction> transactions = service.getTransaction(request.to, request.type);
        transactions.sort(Comparator.comparingInt(CsgoUserTransaction::getTransactTime));
        List<IncomeStatusData.LeaseStatus> statusList = new ArrayList<>();

        if (transactions.isEmpty()) {
            Nlog.info("getIncomeStatus... transactions are empty");
            Result result = new Result(0);
            result.data = new IncomeStatusData(request.from, request.to, request.type, statusList);
            return result;
        }

        long cur = TimeUtil.getDayStartTimeMillis(request.from);
        long end = TimeUtil.getDayStartTimeMillis(request.to);
        int transactionPos = 0;
        double addUp = 0;

        if (cur / 1000 < transactions.get(0).getTransactTime()) {
            // case 1 : requested start day < first transaction day
            // build empty status node and skip
            while (!TimeUtil.isSameDay(transactions.get(0).getTransactTime(), (int) (cur / 1000))) {
                statusList.add(new IncomeStatusData.LeaseStatus(cur));
                cur += TimeUtil.MILLI_DAY;
            }
        } else {
            // case 2 : requested start day > first transaction day
            // addup all incomes before the requested start time
            while (transactionPos < transactions.size() &&
                    transactions.get(transactionPos).getTransactTime() < (int) (cur / 1000)) {
                addUp += transactions.get(transactionPos).getDuration() * transactions.get(transactionPos).getTransactPrice();
                ++transactionPos;
            }
        }

        while (cur <= end) {
            // check transactions at today
            double dailyAddup = 0;
            for (; transactionPos < transactions.size() && TimeUtil.isSameDay(transactions.get(transactionPos).getTransactTime(),  (int) (cur / 1000)); ++transactionPos) {
                dailyAddup += transactions.get(transactionPos).getDuration() * transactions.get(transactionPos).getTransactPrice();
            }
            addUp += dailyAddup;
            statusList.add(new IncomeStatusData.LeaseStatus(cur, addUp, dailyAddup));
            cur += TimeUtil.MILLI_DAY;
        }

        Result result = new Result(0);
        result.data = new IncomeStatusData(request.from, request.to, request.type, statusList);
        Nlog.info("getLeaseIncomeStatus... Successfully handled.");
        return result;
    }

    /**
     * 获取持有的潜在收入
     */
    private Result getHoldingIncomeStatus(RequestGetIncomeStatus request) {
        if (itemCache.isEmpty()) {
            fetchAllItemDetails();
        }
        float cost = 0F;
        List<CsgoUserProperty> properties = service.getUserProperty();
        List<Integer> ids = new ArrayList<>();
        Map<Integer, Float> boughtPriceMap = new HashMap<>();
        for (CsgoUserProperty property : properties) {
            ids.add(property.getItemId());
            boughtPriceMap.put(property.getItemId(), property.getBoughtPrice());
            cost += property.getBoughtPrice();
        }

        // histories sorting priority -- 1) time 2) id 3) transactPrice
        request.to += TimeUtil.SEC_DAY; // if we want data from x to y, the statement should be BETWEEN x AND y + 1
        List<CsgoPriceHistory> histories = service.getPriceHistories(request.from, request.to, ids);
        LinkedList<IncomeStatusData.HoldingStatus> holdings = new LinkedList<>();
        Result result = new Result(0);
        int pos = 0; // points at historical node currently handles
        int end = TimeUtil.getDayStartTimeSeconds(request.to);
        for (int cur = TimeUtil.getDayStartTimeSeconds(request.from); cur <= end; cur += TimeUtil.SEC_DAY) {
            // new day
            if (request.detailedHolding == 1) {
                holdings.add(new IncomeStatusData.DetailedHoldingStatus(cur, cost));
            } else {
                holdings.add(new IncomeStatusData.HoldingStatus(cur, cost));
            }

            if (pos >= histories.size()) {
                // no history prices for today
                continue;
            }
            // handle histories of today
            while (pos < histories.size() && TimeUtil.isSameDay(histories.get(pos).getTransactTime(), cur)) {
                int right = pos, left = pos;
                int id = histories.get(left).getItemId();
                for (; right < histories.size() && histories.get(right).getItemId() == id;) {
                    ++right;
                }
                float itemAvgPrice = 0F;
                // handle data in [pos, right)
                // how to define avg price? 供应会比市场价格低，一些好磨损好贴纸又比市场价格高
//                // skip 3 prices (if exist) and fetch 3 prices (if exist) then avg

//                if (left + 3 < right) {
//                    // skip offers, count 3
//                    left += 3;
//                }
//                if (left + 3 < right) {
//                    // avg count 3
//                    for (int i = 0; i < 3; ++i) {
//                        itemAvgPrice += histories.get(left + i).getSoldPrice();
//                    }
//                    itemAvgPrice /= 3;
//                } else {
//                    itemAvgPrice = histories.get(left).getSoldPrice();
//                }

                itemAvgPrice = histories.get(left).getSoldPrice();
                holdings.getLast().holdingIncome += itemAvgPrice;
                if (request.detailedHolding == 1) {
                    ((IncomeStatusData.DetailedHoldingStatus) holdings.getLast()).detail.add(
                            new ArrayList<>(Arrays.asList(itemCache.get(id).getDesc(), (Math.round((itemAvgPrice - boughtPriceMap.get(id)) * 1000) / 1000))));
                }
                pos = right;
            }
//            holdings.getLast().holdingIncome = getActualGains(holdings.getLast().holdingIncome);
        }
        result.data = new IncomeStatusData(request.from, request.to, request.type, holdings);
        Nlog.info("getHoldingIncomeStatus... Successfully handled.");
        return result;
    }

    /**
     * 除去手续费、提现费的实际所得
     */
    private float getActualGains(float gain) {
        return (float) (gain * 0.965);
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
