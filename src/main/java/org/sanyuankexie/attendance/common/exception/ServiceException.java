package org.sanyuankexie.attendance.common.exception;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
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
}
