package com.nobody.nobodyplace.entity.csgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@IdClass(CsgoPriceHistoryKey.class)
@Table(name = CsgoPriceHistory.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoPriceHistory {

    public final static String TABLE_NAME = "info_csgo_price_history";

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer transactTime; // 秒级别时间戳

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer itemId;

    private Float soldPrice;

    public CsgoPriceHistory() {

    }

    public CsgoPriceHistory(int id, int transactTime, float price) {
        this.transactTime = transactTime;
        this.itemId = id;
        this.soldPrice = price;
    }

    public Integer getTransactTime() {
        return transactTime;
    }

    public void setTransactTime(Integer transactTime) {
        this.transactTime = transactTime;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Float getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(Float soldPrice) {
        this.soldPrice = soldPrice;
    }
}