package com.nobody.nobodyplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = CSGOItem.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})

public class CSGOItem {

    public static final String TABLE_NAME = "info_csgo_item";

    @Id
    int itemId;

    public void setId(int itemId) {
        this.itemId = itemId;
    }

    public int getId() {
        return itemId;
    }
}
