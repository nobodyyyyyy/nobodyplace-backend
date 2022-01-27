package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.dao.csgo.*;
import org.springframework.stereotype.Service;

@Service
public class CsgoService {

    final CsgoDetailedTransactionDAO detailedTransactionDAO;
    final CsgoIncomeAddupDAO incomeAddupDAO;
    final CsgoItemDAO itemDAO;
    final CsgoPrizeHistoryDAO prizeHistoryDAO;
    final CsgoUserPropertyDAO userPropertyDAO;
    final CsgoUserTransactionDAO userTransactionDAO;

    public CsgoService(CsgoDetailedTransactionDAO detailedTransactionDAO,
                       CsgoIncomeAddupDAO incomeAddupDAO, CsgoItemDAO itemDAO,
                       CsgoPrizeHistoryDAO prizeHistoryDAO, CsgoUserPropertyDAO userPropertyDAO,
                       CsgoUserTransactionDAO userTransactionDAO) {
        this.detailedTransactionDAO = detailedTransactionDAO;
        this.incomeAddupDAO = incomeAddupDAO;
        this.itemDAO = itemDAO;
        this.prizeHistoryDAO = prizeHistoryDAO;
        this.userPropertyDAO = userPropertyDAO;
        this.userTransactionDAO = userTransactionDAO;
    }

    // ---------- 市场近期交易交易相关 detailedTransaction ----------


    // ---------- 市场历史交易交易相关 prizeHistory ----------


    // ---------- 商品详细信息相关 item ----------


    // ---------- 用户持有相关 userProperty ----------


    // ---------- 用户交易相关 userTransaction ----------


    // ---------- 用户累计收益相关 incomeAddup ----------

}
