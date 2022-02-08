package com.nobody.nobodyplace.requestbody;

public class RequestAddUserTransaction {
    public int id;
    public int time;
    public float price;  // 总价
    public byte type;  // 0租 1卖
    public int duration;  // 租赁市场

    @Override
    public String toString() {
        return "RequestAddUserTransaction{" +
                "id=" + id +
                ", time=" + time +
                ", price=" + price +
                ", type=" + type +
                ", duration=" + duration +
                '}';
    }
}
