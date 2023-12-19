package com.nobody.nobodyplace.gson.csgo;

import java.util.ArrayList;
import java.util.List;

/**
 * BUFF 首页查询的回包
 */
public class ItemInfosResponse {

    public static final String STATUS_OK = "ok";

    public String code;
    public Data data;

    public static class Data {
        List<DetailedInfo> items;
        Integer total_page;
    }

    public static class DetailedInfo {
        String id;
        String market_hash_name;  // 英文
        String name;  // 中文
        GoodsInfo goods_info;
    }

    public static class GoodsInfo {

        String icon_url;
        InnerInfo info;
        String original_icon_url;  // 图片

    }

    public static class InnerInfo {
        Tags tags;

    }

    public static class Tags {
        TagDesp exterior;
        TagDesp quality;
        TagDesp rarity;
        TagDesp type;
        TagDesp weapon;
    }

    public static class TagDesp {
        String category;
        String internal_name;
        String localized_name;
    }

    public static class ItemInfo {
        String id;
        String nameEng;
        String nameCN;
        String picUrl;
        String exterior; // 磨损等级，中文
        String subType;  // 子类，例如散弹枪
        String weaponType;  // 枪械类型子类，例如 MAG-7

        public ItemInfo() {}

        public ItemInfo(String id, String nameEng, String nameCN, String picUrl,
                        String exterior, String subType) {
            this(id, nameEng, nameCN, picUrl, exterior, subType, "");
        }

        public ItemInfo(String id, String nameEng, String nameCN, String picUrl,
                        String exterior, String subType, String weaponType) {
            this.id = id;
            this.nameEng = nameEng;
            this.nameCN = nameCN;
            this.picUrl = picUrl;
            this.exterior = exterior;
            this.subType = subType;
            this.weaponType = weaponType;
        }

        public static String[] getLabelEntries() {
            return new String[]{"id", "nameEng", "nameCN", "picUrl", "exterior", "subType", "weaponType"};
        }

        public String[] getValueEntries() {
            return new String[]{id, nameEng, nameCN, picUrl, exterior, subType, weaponType};
        }

        @Override
        public String toString() {
            return "ItemInfo{" +
                    "id='" + id + '\'' +
                    ", nameEng='" + nameEng + '\'' +
                    ", nameCN='" + nameCN + '\'' +
                    ", picUrl='" + picUrl + '\'' +
                    ", exterior='" + exterior + '\'' +
                    ", subType='" + subType + '\'' +
                    ", weaponType='" + weaponType + '\'' +
                    '}';
        }
    }

    public String[] getLabels() {
        return ItemInfo.getLabelEntries();
    }

    public List<ItemInfo> getItemInfos() {
        List<ItemInfo> ret = new ArrayList<>();
        for (DetailedInfo info : data.items) {
            try {
                ItemInfo temp = new ItemInfo(info.id, info.name, info.market_hash_name,
                        info.goods_info.icon_url,
                        info.goods_info.info.tags.exterior.localized_name,
                        info.goods_info.info.tags.type.localized_name,
                        info.goods_info.info.tags.weapon.localized_name);
                ret.add(temp);
            } catch (Exception e) {
                try {
                    ItemInfo temp = new ItemInfo(info.id, info.name, info.market_hash_name,
                            info.goods_info.icon_url,
                            info.goods_info.info.tags.exterior.localized_name,
                            info.goods_info.info.tags.type.localized_name);
                    ret.add(temp);
                } catch (Exception e1) {
                    System.out.println("getItemInfos... Can not parse");
                }
            }
        }
        return ret;
    }

    public List<String[]> getItemInfosStringArray() {
        List<String[]> ret = new ArrayList<>();
        for (DetailedInfo info : data.items) {
            try {
                ret.add(new String[]{info.id, info.name, info.market_hash_name,
                        info.goods_info.icon_url,
                        info.goods_info.info.tags.exterior.localized_name,
                        info.goods_info.info.tags.type.localized_name,
                        info.goods_info.info.tags.weapon.localized_name});
            } catch (Exception e) {
                try {
                    ret.add(new String[]{info.id, info.name, info.market_hash_name,
                            info.goods_info.icon_url,
                            info.goods_info.info.tags.exterior.localized_name,
                            info.goods_info.info.tags.type.localized_name});
                } catch (Exception e1) {
                    System.out.println("getItemInfos... Can not parse");
                }
            }
        }
        return ret;
    }

    public int getPage() {
        return data.total_page;
    }
}
