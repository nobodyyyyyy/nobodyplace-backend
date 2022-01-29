package com.nobody.nobodyplace.response.csgo;

import java.util.List;

/**
 * 与 PriceHistoryResponse 相关，与前端的格式约定
 */
public class ItemHistoryPrice {
    public int id;
    public List<List<Float>> prices;
}
