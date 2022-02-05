package com.nobody.nobodyplace.entity.csgo;

import java.io.Serializable;

public class CsgoUserTransactionKey implements Serializable {
    private Integer itemId;
    private Integer transactTime;

    public CsgoUserTransactionKey() {

    }

    public CsgoUserTransactionKey(Integer id, Integer time) {
        this.itemId = id;
        this.transactTime = time;
    }
}