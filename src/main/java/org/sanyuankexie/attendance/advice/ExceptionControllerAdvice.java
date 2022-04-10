package org.sanyuankexie.attendance.advice;

import lombok.extern.slf4j.Slf4j;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> customExceptionHandler(ServiceException e) {
        log.error(e.getMsg());
        return ResultHelper.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<Object> exceptionHandler(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ResultHelper.error(CExceptionEnum.UNKNOWN.getCode(), CExceptionEnum.UNKNOWN.getMsg());
    }
}
