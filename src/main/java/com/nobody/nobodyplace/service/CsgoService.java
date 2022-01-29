package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.controller.SearchSuggestionController;
import com.nobody.nobodyplace.dao.csgo.*;
import com.nobody.nobodyplace.entity.csgo.CsgoPriceHistory;
import com.nobody.nobodyplace.response.csgo.ItemHistoryPrice;
import com.nobody.nobodyplace.response.csgo.PriceHistoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                // 以免 api 回数据格式改变
            }
        }
        priceHistoryDAO.saveAll(entities);
    }

//    public List<ItemHistoryPrice> get


    // ---------- 商品详细信息相关 item ----------


    // ---------- 用户持有相关 userProperty ----------


    // ---------- 用户交易相关 userTransaction ----------


    // ---------- 用户累计收益相关 incomeAddup ----------

}
