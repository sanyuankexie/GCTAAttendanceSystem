package org.sanyuankexie.attendance.common.exception;

public enum CExceptionEnum {
    UNKNOWN(-1, "Unknown exception"),
    USER_NO_EXIST(-200, "用户名不存在"),
    USER_ONLINE(-201, "不许重复签到"),
    USER_OFFLINE(-202, "宁没有签到噢"),
    USER_ID_NO_EXIST(-203, "学号不存在"),
    USER_C_OFFLINE(-204, "宁想举报的人不在线"),
    ;
    private Integer code;
    private String msg;

    CExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
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
