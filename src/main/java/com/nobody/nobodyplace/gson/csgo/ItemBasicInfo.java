package com.nobody.nobodyplace.gson.csgo;

import java.util.List;

/**
 * id 和 商品名的绑定回包
 */
public class ItemBasicInfo {

    public static final String STATUS_OK = "ok";

    public String code;
    public Data data;

    public static class Data {
        public List<ItemInfo> suggestions;
    }

    public static class ItemInfo {
        String goods_ids;
        String option;
    }

    public List<ItemInfo> getItemInfos() {
        if (data != null) {
            return data.suggestions;
        } else {
            return null;
        }
    }
}
