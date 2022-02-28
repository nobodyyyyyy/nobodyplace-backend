package com.nobody.nobodyplace.response;

public class Result {

    public int code;
    public String msg;
    public Data data;

    public Result() {
        this.code = -1;
    }

    public Result(int code) {
        this.code = code;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
