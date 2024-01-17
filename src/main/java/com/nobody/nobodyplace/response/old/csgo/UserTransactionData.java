package com.nobody.nobodyplace.response.old.csgo;

import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.response.old.Data;

import java.util.ArrayList;
import java.util.List;

public class UserTransactionData extends Data {

    public List<CsgoUserTransaction> records;

    public UserTransactionData() {
        records = new ArrayList<>();
    }

    public UserTransactionData(List<CsgoUserTransaction> transactions) {
        records = transactions;
    }
}
