package com.nobody.nobodyplace.requestbody;

public class RequestGetIncomeStatus {
    public static final int TYPE_LEASE = 0;
    public static final int TYPE_SELL = 1;
    public static final int TYPE_HOLDING = 2;
    public static final int TYPE_ALL = 3;

    public int from;
    public int to;

    // type: 0租赁,1卖出,2潜在,3总共
    public int type;

    // 是否走 api 更新租赁信息
    public byte fetch;
}
