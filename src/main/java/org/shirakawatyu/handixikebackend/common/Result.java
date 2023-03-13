package org.shirakawatyu.handixikebackend.common;

import java.io.Serializable;

public class Result implements Serializable {
    private boolean success;
    private int code;
    private String msg;
    private Object data;

    private Result() {}

    public static Result ok() {
        Result result = new Result();
        result.setSuccess(true);
        return result;
    }

    public static Result fail() {
        Result result = new Result();
        result.setSuccess(false);
        return result;
    }

    public Result code(int code) {
        this.code = code;
        return this;
    }

    public Result msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result data(Object data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
