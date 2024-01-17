package com.nobody.nobodyplace.response.old.csgo;

import com.nobody.nobodyplace.requestbody.RequestGetIncomeStatus;
import com.nobody.nobodyplace.response.old.Data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IncomeStatusData extends Data {

    /**
     * {@link RequestGetIncomeStatus 中的 type}
     */
    public int type;

    public int from;
    public int to;
    public Object incomes;

    public static DecimalFormat df = new DecimalFormat("#.00");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public IncomeStatusData(int from, int to, int type, Object incomes) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.incomes = incomes;
    }

    public static class Status {

        public int time; // [秒]级别时间戳，代表一天的开始
        public String timeDesc;

        public Status() {}

        public Status(int time) {
            this.time = time;
            this.timeDesc = sdf.format(new Date(time * 1000L));
        }

        public Status(long time) {

        }
    }

    public static class LeaseStatus extends Status{

        public String addup; // 截止当天，累计收入
        public String dailyIncome;

        public void init(double addup, double dailyIncome) {
            this.addup = formatZero(df.format(addup));
            this.dailyIncome = formatZero(df.format(dailyIncome));
        }

        public LeaseStatus(long time) {
            super(time);
            init(0D, 0D);
        }

        public LeaseStatus(long time, double addup, double dailyIncome) {
            super(time);
            init(addup, dailyIncome);
        }

        private String formatZero(String str) {
            if (str == null || str.isEmpty() || str.startsWith(".")) {
                return "0";
            }
            return str;
        }
    }

    public static class HoldingStatus extends Status {

        public float holdingIncome;
        public float cost; // 目前是不会动态改变的。。。。

        public HoldingStatus(int time, float cost) {
            super(time);
            this.cost = cost;
        }
    }

    public static class DetailedHoldingStatus extends HoldingStatus {

        /**
         * [[商品名, 收益/亏损], [], ...]
         */
        public List<List<Object>> detail = new ArrayList<>();

        public DetailedHoldingStatus(int time, float cost) {
            super(time, cost);
        }
    }


}
