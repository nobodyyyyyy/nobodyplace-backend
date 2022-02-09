package com.nobody.nobodyplace.response.csgo;

import com.nobody.nobodyplace.requestbody.RequestGetIncomeStatus;
import com.nobody.nobodyplace.response.Data;

import java.util.List;

public class IncomeStatusData extends Data {

    /**
     * {@link RequestGetIncomeStatus 中的 type}
     */
    public int type;

    public int from;
    public int to;
    public List<IncomeStatus> incomes;

    public IncomeStatusData(int from, int to, int type, List<IncomeStatus> incomes) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.incomes = incomes;
    }

    public static class IncomeStatus {
        public int time; // 秒级别时间戳，代表一天的开始
        public double addup; // 截止当天，累计收入，类型为上述 type
        public double dailyIncome; // 当天的 type 类型收入
    }
}
