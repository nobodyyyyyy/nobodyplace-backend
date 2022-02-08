package com.nobody.nobodyplace.gson.csgo;

import java.util.List;

public class LeaseRecordResponse {
    public Integer Code;
    public String Msg;
    public List<LeaseRecord> Data;

    public static class LeaseRecord {
        public String Name; // 唯一可以对应查询的一项，对应 csgoItem 的 desc
        public String OrderNo;
        public String TradeTypeName;
        public Float LeaseUnitPrice;  // 一天多少
        public Integer LeaseDays;
        public String ReturnTime;
    }
}
