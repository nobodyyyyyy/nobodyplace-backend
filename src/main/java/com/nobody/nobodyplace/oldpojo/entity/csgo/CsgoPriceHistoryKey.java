package com.nobody.nobodyplace.oldpojo.entity.csgo;

import java.io.Serializable;

public class CsgoPriceHistoryKey implements Serializable {
    private Integer transactTime;
    private Integer itemId;

    public CsgoPriceHistoryKey() {

    }

    public CsgoPriceHistoryKey(Integer id, Integer time) {
        this.itemId = id;
        this.transactTime = time;
    }
}
