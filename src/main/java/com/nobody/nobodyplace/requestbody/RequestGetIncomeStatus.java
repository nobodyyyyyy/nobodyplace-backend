package com.nobody.nobodyplace.requestbody;

public class RequestGetIncomeStatus {
    public static final byte TYPE_LEASE = 0;
    public static final byte TYPE_SELL = 1;
    public static final byte TYPE_HOLDING = 2;
    public static final byte TYPE_ALL = 3;

    /**
     * 一定要是整点啊啊啊啊啊啊
     */
    public int from;
    public int to;

    // type: 0租赁,1卖出,2潜在,3总共
    public byte type = -1;

    // 是否走 api 更新租赁信息
    public byte fetch;
}
