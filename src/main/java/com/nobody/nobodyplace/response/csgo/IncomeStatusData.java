package com.nobody.nobodyplace.response.csgo;

import com.nobody.nobodyplace.requestbody.RequestGetIncomeStatus;
import com.nobody.nobodyplace.response.Data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IncomeStatusData extends Data {

    /**
     * {@link RequestGetIncomeStatus 中的 type}
     */
    public int type;

    public int from;
    public int to;
    public List<IncomeStatus> incomes;

    public static DecimalFormat df = new DecimalFormat("#.00");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public IncomeStatusData(int from, int to, int type, List<IncomeStatus> incomes) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.incomes = incomes;
    }

    public static class IncomeStatus {
        public int time; // [秒]级别时间戳，代表一天的开始
        public String timeDesc;
        public String addup; // 截止当天，累计收入，类型为上述 type
        public String dailyIncome; // 当天的 type 类型收入

        public IncomeStatus() {

        }

        public void init(int time, double addup, double dailyIncome) {
            this.time = time;
            this.timeDesc = sdf.format(new Date(time * 1000L));
            this.addup = formatZero(df.format(addup));
            this.dailyIncome = formatZero(df.format(dailyIncome));
        }

        public IncomeStatus(long time) {
            init((int) (time / 1000), 0D, 0D);
        }

        public IncomeStatus(int time, double addup, double dailyIncome) {
            init(time, addup, dailyIncome);
        }

        public IncomeStatus(long time, double addup, double dailyIncome) {
            init((int) (time / 1000), addup, dailyIncome);
        }

        private String formatZero(String str) {
            if (str == null || str.isEmpty() || str.startsWith(".")) {
                return "0";
            }
            return str;
        }
    }
}
