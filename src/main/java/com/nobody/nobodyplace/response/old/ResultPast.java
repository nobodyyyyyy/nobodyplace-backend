package com.nobody.nobodyplace.response.old;

import com.nobody.nobodyplace.response.old.Data;

public class ResultPast {

    public int code;
    public String msg;
    public Data data;

    public ResultPast() {
        this.code = -1;
    }

    public ResultPast(int code) {
        this.code = code;
    }

    public ResultPast(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
