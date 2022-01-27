package com.nobody.nobodyplace.entity.csgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(CsgoPrizeHistoryKey.class)
@Table(name = CsgoPrizeHistory.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoPrizeHistory {

    public final static String TABLE_NAME = "info_csgo_prize_history";

    @Id
    private Integer transactTime;

    @Id
    private Integer itemId;

    private Float soldPrize;

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

    public Float getSoldPrize() {
        return soldPrize;
    }

    public void setSoldPrize(Float soldPrize) {
        this.soldPrize = soldPrize;
    }
}