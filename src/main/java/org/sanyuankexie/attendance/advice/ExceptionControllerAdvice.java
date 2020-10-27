package org.sanyuankexie.attendance.advice;

import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseBody
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //todo
    public ResultVO<Object> customExceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            return ResultHelper.error(serviceException.getCode(), serviceException.getMsg());
        } else {
            return ResultHelper.error(CExceptionEnum.UNKNOWN.getCode(), CExceptionEnum.UNKNOWN.getMsg());
        }
    }
}
