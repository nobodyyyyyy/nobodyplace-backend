package com.nobody.nobodyplace.oldpojo.entity.csgo;

import java.io.Serializable;

public class CsgoDetailedTransactionKey implements Serializable {
    private Integer itemId;

    private Integer transactTime;

    private String assetid;

    public CsgoDetailedTransactionKey(Integer id, Integer time, String assetid) {
        this.itemId = id;
        this.transactTime = time;
        this.assetid = assetid;
    }
}
