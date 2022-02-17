package com.pract.utils;

import lombok.Data;

@Data
public class Result {
    private Boolean flag;
    private String code;
    private String msg;
    private Object data;

    public Result() {
    }

    public Result(Boolean flag) {
        this.flag = flag;
    }

    public Result(String message) {
        this.msg = message;
    }

    public Result(Boolean flag, String message) {
        this.flag = flag;
        this.msg = message;
    }

    public Result(Boolean flag, Object data) {
        this.flag = flag;
        this.data = data;
    }

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.setFlag(true);
        result.setCode("1");
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result error(String msg, Object data) {
        Result result = new Result();
        result.setFlag(false);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result error(String code, String msg, Object data) {
        Result result = error(msg, data);
        result.setCode(code);
        return result;
    }
}
