package com.nobody.nobodyplace.entity.csgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = CsgoUserProperty.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoUserProperty {

    public final static String TABLE_NAME = "info_csgo_user_property";

    @Id
    private Integer itemId;

    private Float boughtPrize;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Float getBoughtPrize() {
        return boughtPrize;
    }

    public void setBoughtPrize(Float boughtPrize) {
        this.boughtPrize = boughtPrize;
    }
}