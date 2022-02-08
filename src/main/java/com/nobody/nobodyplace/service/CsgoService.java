package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.dao.csgo.*;
import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistory;
import com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestAddUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestGetUserTransaction;
import com.nobody.nobodyplace.requestbody.RequestItemHistoryPrice;
import com.nobody.nobodyplace.response.csgo.HistoryPriceItemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CsgoService {

    private static final Logger Nlog = LoggerFactory.getLogger(CsgoService.class);

    final CsgoDetailedTransactionDAO detailedTransactionDAO;
    final CsgoIncomeAddupDAO incomeAddupDAO;
    final CsgoItemDAO itemDAO;
    final CsgoPriceHistoryDAO priceHistoryDAO;
    final CsgoUserPropertyDAO userPropertyDAO;
    final CsgoUserTransactionDAO userTransactionDAO;

    public CsgoService(CsgoDetailedTransactionDAO detailedTransactionDAO,
                       CsgoIncomeAddupDAO incomeAddupDAO, CsgoItemDAO itemDAO,
                       CsgoPriceHistoryDAO priceHistoryDAO, CsgoUserPropertyDAO userPropertyDAO,
                       CsgoUserTransactionDAO userTransactionDAO) {
        this.detailedTransactionDAO = detailedTransactionDAO;
        this.incomeAddupDAO = incomeAddupDAO;
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


    // ---------- 商品详细信息相关 item ----------

    @Transactional
    public List<CsgoItem> batchGetItemInfo(List<Integer> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }
        return itemDAO.getCsgoItemsByItemIdIn(requests);
    }

    // ---------- 用户持有相关 userProperty ----------


    // ---------- 用户交易相关 userTransaction ----------

    public void addTransaction(RequestAddUserTransaction request) {
        CsgoUserTransaction transaction =
                new CsgoUserTransaction(request.id, request.time, request.price, request.type, request.duration);
        userTransactionDAO.save(transaction);
    }

    public List<CsgoUserTransaction> getTransaction(RequestGetUserTransaction request) {
        return userTransactionDAO.getCsgoUserTransactionsByTransactTimeBetween(request.from, request.to);
    }

    // ---------- 用户累计收益相关 incomeAddup ----------

}
