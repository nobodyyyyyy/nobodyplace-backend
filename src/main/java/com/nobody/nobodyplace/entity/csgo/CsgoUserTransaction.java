package com.nobody.nobodyplace.entity.csgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(CsgoUserTransactionKey.class)
@Table(name = CsgoUserTransaction.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoUserTransaction {

    public final static String TABLE_NAME = "info_csgo_user_transaction";

    @Id
    private Integer itemId;

    @Id
    private Integer transactTime;

    private Float transactPrice;

    private Byte transactType;

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
}