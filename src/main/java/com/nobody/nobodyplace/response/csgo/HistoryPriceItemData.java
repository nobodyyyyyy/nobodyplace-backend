package com.nobody.nobodyplace.response.csgo;

import com.nobody.nobodyplace.response.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 与 PriceHistoryData 相关，与前端的格式约定
 */
public class HistoryPriceItemData extends Data {
    public int id;
    public int from;
    public int to;
    public List<List<Object>> prices = new ArrayList<>();
}
