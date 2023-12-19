package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.dao.csgo.*;
import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistory;
import com.nobody.nobodyplace.entity.csgo.CsgoUserProperty;
import com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestGetUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestItemHistoryPrice;
import com.nobody.nobodyplace.response.csgo.HistoryPriceItemData;
import com.nobody.nobodyplace.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class CsgoService {

    private static final Logger Nlog = LoggerFactory.getLogger(CsgoService.class);

    final CsgoDetailedTransactionDAO detailedTransactionDAO;
    final CsgoItemDAO itemDAO;
    final CsgoPriceHistoryDAO priceHistoryDAO;
    final CsgoUserPropertyDAO userPropertyDAO;
    final CsgoUserTransactionDAO userTransactionDAO;

    public CsgoService(CsgoDetailedTransactionDAO detailedTransactionDAO,
                       CsgoItemDAO itemDAO,
                       CsgoPriceHistoryDAO priceHistoryDAO,
                       CsgoUserPropertyDAO userPropertyDAO,
                       CsgoUserTransactionDAO userTransactionDAO) {
        this.detailedTransactionDAO = detailedTransactionDAO;
        this.itemDAO = itemDAO;
        this.priceHistoryDAO = priceHistoryDAO;
        this.userPropertyDAO = userPropertyDAO;
        this.userTransactionDAO = userTransactionDAO;
    }

    // ---------- 市场近期交易交易相关 detailedTransaction ----------


    // ---------- 市场历史交易交易相关 priceHistory ----------

    @Transactional
    public void batchAddPriceHistory(int itemId, List<List<Float>> historyList) {
        List<CsgoPriceHistory> entities = new ArrayList<>();
        for (List<Float> node : historyList) {
            try {
                entities.add(new CsgoPriceHistory(itemId, (int) (node.get(0) / 1000), node.get(1)));
            } catch (Exception e) {
                Nlog.info("batchAddPriceHistory... err: " + e.toString());
                // 以免 api 回包数据格式改变
            }
        }
        priceHistoryDAO.saveAll(entities);
    }

    @Transactional
    public List<HistoryPriceItemData> batchGetPriceHistories(List<RequestItemHistoryPrice> requests) {
        List<HistoryPriceItemData> ret = new ArrayList<>();
        if (requests == null || requests.isEmpty()) {
            return ret;
        }
        for (RequestItemHistoryPrice request : requests) {
            List<CsgoPriceHistory> histories =
                    priceHistoryDAO.findAllByItemIdEqualsAndTransactTimeBetweenOrderByTransactTime(request.id, request.from, request.to);
            HistoryPriceItemData item = new HistoryPriceItemData();
            item.id = request.id;
            item.from = request.from;
            item.to = request.to;
            for (CsgoPriceHistory history : histories) {
                item.prices.add((Arrays.asList(history.getTransactTime(), history.getSoldPrice())));
            }
            ret.add(item);
        }
        return ret;
    }

    /**
     * 为计算每天的潜在收入而提供的接口。这里得到的时间会有预处理，转换成每天的起始时间。
     */
    public List<CsgoPriceHistory> getPriceHistories(int from, int to, List<Integer> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<CsgoPriceHistory> res = priceHistoryDAO.findAllByItemIdInAndTransactTimeBetween(itemIds, from, to);
        for (CsgoPriceHistory history : res) {
            history.setTransactTime(TimeUtil.getDayStartTimeSeconds(history.getTransactTime()));
        }
        // already sorted by time and item
        res.sort(new Comparator<CsgoPriceHistory>() {
            @Override
            public int compare(CsgoPriceHistory o1, CsgoPriceHistory o2) {
                if (!o1.getTransactTime().equals(o2.getTransactTime())) {
                    return Integer.compare(o1.getTransactTime(), o2.getTransactTime());
                } else if (!o1.getItemId().equals(o2.getItemId())) {
                    return Integer.compare(o1.getItemId(), o2.getItemId());
                } else {
                    return Float.compare(o1.getSoldPrice(), o2.getSoldPrice());
                }
            }
        });
        return res;
    }


    // ---------- 商品详细信息相关 item ----------

    @Transactional
    public List<CsgoItem> batchGetItemInfo(List<Integer> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }
        return itemDAO.getCsgoItemsByItemIdIn(requests);
    }

    @Transactional
    public List<CsgoItem> getAllItemInfo() {
        return itemDAO.findAll();
    }

    // ---------- 用户持有相关 userProperty ----------

    public List<CsgoUserProperty> getUserProperty() {
        return userPropertyDAO.findAll();
    }


    // ---------- 用户交易相关 userTransaction ----------

    public void addTransaction(List<CsgoUserTransaction> transactions) {
        userTransactionDAO.saveAll(transactions);
    }

    public List<CsgoUserTransaction> getTransaction(RequestGetUserTransaction request) {
        return userTransactionDAO.getCsgoUserTransactionsByTransactTimeBetween(request.from, request.to);
    }

    public List<CsgoUserTransaction> getTransaction(int endTime, byte type) {
        return userTransactionDAO.getCsgoUserTransactionsByTransactTimeLessThanAndTransactTypeEquals(endTime, type);
    }

}
