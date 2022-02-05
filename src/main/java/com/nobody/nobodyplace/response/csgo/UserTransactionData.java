package com.nobody.nobodyplace.response.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.response.Data;

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
