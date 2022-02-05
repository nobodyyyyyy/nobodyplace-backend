package com.nobody.nobodyplace.entity.csgo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.thymeleaf.util.TextUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = CsgoItem.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoItem {

    public final static String TABLE_NAME = "info_csgo_item";

    @Id
    private Integer itemId;

    private String itemType;

    private String itemName;

    /**
     * 0:崭新出场 1:略有磨损 2:久经沙场 3:破损不堪 4:战痕累累
     */
    private Byte itemWearType;

    private String displayUrl;

    private Integer addedTime;

    private Boolean isStatTrak;

    private String desc;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType == null ? null : itemType.trim();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    public Byte getItemWearType() {
        return itemWearType;
    }

    public void setItemWearType(Byte itemWearType) {
        this.itemWearType = itemWearType;
    }

    public static byte getItemWearType4Storage(String itemWearType) {
        if (TextUtils.equals(false, itemWearType, "崭新出场")) {
            return 0;
        } else if (TextUtils.equals(false, itemWearType, "崭新出场")) {
            return 1;
        } else if (TextUtils.equals(false, itemWearType, "久经沙场")) {
            return 2;
        } else if (TextUtils.equals(false, itemWearType, "破损不堪")) {
            return 3;
        } else if (TextUtils.equals(false, itemWearType, "战痕累累")) {
            return 4;
        } else {
            return -1;
        }
    }

    public static String getItemWearTypeDesc(Byte itemWearType) {
        if (itemWearType == 0) {
            return "崭新出场";
        } else if (itemWearType == 1) {
            return "略有磨损";
        } else if (itemWearType == 2) {
            return "久经沙场";
        } else if (itemWearType == 3) {
            return "破损不堪";
        } else if (itemWearType == 4) {
            return "战痕累累";
        } else {
            return "";
        }
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl == null ? null : displayUrl.trim();
    }

    public Integer getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Integer addedTime) {
        this.addedTime = addedTime;
    }

    public Boolean getIsStatTrak() {
        return isStatTrak;
    }

    public void setIsStatTrak(Boolean isStatTrak) {
        this.isStatTrak = isStatTrak;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}