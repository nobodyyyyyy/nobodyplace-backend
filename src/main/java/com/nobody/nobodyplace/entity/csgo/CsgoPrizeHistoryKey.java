package com.nobody.nobodyplace.entity.csgo;

import java.io.Serializable;

public class CsgoPrizeHistoryKey implements Serializable {
    private Integer transactTime;
    private Integer itemId;

    public CsgoPrizeHistoryKey(Integer id, Integer time) {
        this.itemId = id;
        this.transactTime = time;
    }
}