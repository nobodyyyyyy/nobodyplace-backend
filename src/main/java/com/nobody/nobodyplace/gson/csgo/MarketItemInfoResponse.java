package com.nobody.nobodyplace.gson.csgo;

import java.util.List;

public class MarketItemInfoResponse {

    public static final String STATUS_OK = "ok";

    public String code;
    public Data data;

    public static class Data {
        public List<MarketItemDetailedInfo> items;
    }

    public static class MarketItemDetailedInfo {
        public String price;
        public long created_at;
        public AssetInfo asset_info;
    }

    /**
     * 主要包含当前对象的属性，例如贴纸、磨损、皮肤编号等……
     */
    public static class AssetInfo {
        public MarketItemAssetInfo info;
        public String goods_id;
        public String assetid;
        public String classid;
        public String paintwear;

        public static class MarketItemAssetInfo {
            public String icon_url; // 展示的网络图片地址
            public int paintindex;
            public int paintseed;
            public List<StickerInfo> stickers;
        }

        public static class StickerInfo {
            public String img_url;
            public String name;
            public int slot;
            public String sticker_id;
            public int wear;
        }
    }
}
