package org.sanyuankexie.attendance.common.exception;

public class ServiceException extends RuntimeException {
    private Integer code;
    private String msg;

    public ServiceException(CExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
    }

    public ServiceException(CExceptionEnum exceptionEnum, Long userId) {
        this.code = exceptionEnum.getCode();
        this.msg = "<" + userId + ">" + exceptionEnum.getMsg();
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
