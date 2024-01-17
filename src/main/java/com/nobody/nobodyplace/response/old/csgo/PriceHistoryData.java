package com.nobody.nobodyplace.response.old.csgo;

import com.nobody.nobodyplace.response.old.Data;

import java.util.List;

public class PriceHistoryData extends Data {
    public List<HistoryPriceItemData> infos;

    public PriceHistoryData(List<HistoryPriceItemData> infos) {
        this.infos = infos;
    }
}
