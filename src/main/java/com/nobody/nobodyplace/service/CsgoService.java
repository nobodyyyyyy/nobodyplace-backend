package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.dao.csgo.*;
import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistory;
import com.nobody.nobodyplace.requestbody.RequestItemHistoryPrice;
import com.nobody.nobodyplace.response.csgo.ItemHistoryPrice;
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

    @Transactional()
    public List<ItemHistoryPrice> batchGetPriceHistories(List<RequestItemHistoryPrice> requests) {
        List<ItemHistoryPrice> ret = new ArrayList<>();
        if (requests == null || requests.isEmpty()) {
            return ret;
        }
        for (RequestItemHistoryPrice request : requests) {
            List<CsgoPriceHistory> histories = priceHistoryDAO.findAllByItemIdEqualsAndTransactTimeBetweenOrderByTransactTime(request.id, request.from, request.to);
            ItemHistoryPrice item = new ItemHistoryPrice();
            item.id = request.id;
            for (CsgoPriceHistory history : histories) {
                item.prices.add((Arrays.asList(history.getTransactTime(), history.getSoldPrice())));
            }
            ret.add(item);
        }
        return ret;
    }


    // ---------- 商品详细信息相关 item ----------


    // ---------- 用户持有相关 userProperty ----------


    // ---------- 用户交易相关 userTransaction ----------


    // ---------- 用户累计收益相关 incomeAddup ----------

}
