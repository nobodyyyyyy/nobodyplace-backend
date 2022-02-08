package com.nobody.nobodyplace.response.csgo;

import java.util.ArrayList;
import java.util.List;

/**
 * 与 PriceHistoryData 相关，与前端的格式约定
 */
public class HistoryPriceItemData {
    public int id;
    public int from;
    public int to;
    public List<List<Object>> prices = new ArrayList<>();
}
