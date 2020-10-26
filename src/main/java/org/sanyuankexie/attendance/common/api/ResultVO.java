package org.sanyuankexie.attendance.common.api;

import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

public class ResultVO<T> implements Serializable {
    private T data;
    private Integer code;
    private String msg;

    public ResultVO(T data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
