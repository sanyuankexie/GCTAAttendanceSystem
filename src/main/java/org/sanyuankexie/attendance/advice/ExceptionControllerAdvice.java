package org.sanyuankexie.attendance.advice;

import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseBody
    @Order(1)
    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> customExceptionHandler(ServiceException e) {
        e.printStackTrace();
        return ResultHelper.error(e.getCode(), e.getMsg());
    }

    @ResponseBody
    @Order(2)
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<Object> exceptionHandler(Exception e) {
        e.printStackTrace();
        return ResultHelper.error(CExceptionEnum.UNKNOWN.getCode(), CExceptionEnum.UNKNOWN.getMsg());
    }
}
