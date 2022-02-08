package com.nobody.nobodyplace.entity.csgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@IdClass(CsgoUserTransactionKey.class)
@Table(name = CsgoUserTransaction.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoUserTransaction {

    public final static String TABLE_NAME = "info_csgo_user_transaction";
    public final static String ITEMID_JOIN_NAME = "itemId";

    @Id
    private Integer itemId;

    @Id
    private String transactId;

    private Float transactPrice;

    // 0:租赁；1:卖出
    private Byte transactType;

    private Integer duration;

    private Integer transactTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = ITEMID_JOIN_NAME, insertable=false, updatable=false)
    private CsgoItem item;

    public CsgoUserTransaction() {

    }

    public CsgoUserTransaction(int id, String transactId, int time, float price, byte type, int duration) {
        this.itemId = id;
        this.transactId = transactId;
        this.transactTime = time;
        this.transactPrice = price;
        this.transactType = type;
        this.duration = duration;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getTransactId() {
        return transactId;
    }

    public void setTransactId(String transactId) {
        this.transactId = transactId;
    }

    public Float getTransactPrice() {
        return transactPrice;
    }

    public void setTransactPrice(Float transactPrice) {
        this.transactPrice = transactPrice;
    }

    public Byte getTransactType() {
        return transactType;
    }

    public void setTransactType(Byte transactType) {
        this.transactType = transactType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getTransactTime() {
        return transactTime;
    }

    public void setTransactTime(Integer transactTime) {
        this.transactTime = transactTime;
    }

    public CsgoItem getItem() {
        return item;
    }

    public void setItem(CsgoItem item) {
        this.item = item;
    }
}