package com.nobody.nobodyplace.gson.csgo;

import java.util.ArrayList;
import java.util.List;

public class MarketHistoryItemInfoResponse {
    public static final String STATUS_OK = "ok";

    public String code;
    public Data data;

    public static class Data {
        public String currency;
        public String currency_symbol;
        public int days;
        public List<List<Float>> price_history;
    }

    public List<List<Float>> getPriceHistory() {
        if (data == null || data.price_history == null) {
            return new ArrayList<>();
        }
        return data.price_history;
    }
}
