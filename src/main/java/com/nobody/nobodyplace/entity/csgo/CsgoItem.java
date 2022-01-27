package com.nobody.nobodyplace.entity.csgo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    private Byte itemWearType;

    private String displayUrl;

    private Integer addedTime;

    private Boolean isStatTrak;

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
}