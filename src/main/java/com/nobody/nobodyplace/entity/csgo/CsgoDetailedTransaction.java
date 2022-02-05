package com.nobody.nobodyplace.entity.csgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@IdClass(CsgoDetailedTransactionKey.class)
@Table(name = CsgoDetailedTransaction.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoDetailedTransaction {

    public final static String TABLE_NAME = "info_csgo_detailed_transaction";

    @Id
    private Integer itemId;

    @Id
    private Integer transactTime;

    @Id
    private String assetid;

    private Float soldPrice;

    private String itemWear;

    private String fadePercent;

    private Integer addedTime;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getTransactTime() {
        return transactTime;
    }

    public void setTransactTime(Integer transactTime) {
        this.transactTime = transactTime;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid == null ? null : assetid.trim();
    }

    public Float getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(Float soldPrice) {
        this.soldPrice = soldPrice;
    }

    public String getItemWear() {
        return itemWear;
    }

    public void setItemWear(String itemWear) {
        this.itemWear = itemWear == null ? null : itemWear.trim();
    }

    public String getFadePercent() {
        return fadePercent;
    }

    public void setFadePercent(String fadePercent) {
        this.fadePercent = fadePercent == null ? null : fadePercent.trim();
    }

    public Integer getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Integer addedTime) {
        this.addedTime = addedTime;
    }
}