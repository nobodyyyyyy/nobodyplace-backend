package com.nobody.nobodyplace.gson.csgo;

import com.nobody.nobodyplace.pojo.dto.CSGOItemHistoryPriceDTO;
import com.nobody.nobodyplace.utils.TimeUtil;

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

    public List<List<Float>> getPriceHistoryRawList() {
        if (data == null || data.price_history == null) {
            return new ArrayList<>();
        }
        return data.price_history;
    }

    public List<CSGOItemHistoryPriceDTO> getPriceHistory(long itemId) {
        List<CSGOItemHistoryPriceDTO> ret = new ArrayList<>();
        if (data != null && data.price_history != null) {
            for (List<Float> ele : data.price_history) {
                long time = (long) ele.get(0).floatValue();
                ret.add(CSGOItemHistoryPriceDTO.builder()
                        .itemId(itemId)
                        .time(TimeUtil.timestampToDatetime(time))
                        .price(ele.get(1))
                        .build());
            }
        }
        return ret;
    }
}
