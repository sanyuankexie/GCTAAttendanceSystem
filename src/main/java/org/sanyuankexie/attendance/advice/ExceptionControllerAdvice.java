package org.sanyuankexie.attendance.advice;

import com.alibaba.fastjson.JSONObject;
import com.therainisme.AmeBox.logUtil.LogFactory;
import com.therainisme.AmeBox.logUtil.Logger;
import com.therainisme.AmeBox.logUtil.enums.LogOutputType;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.exception.ServiceException;
import org.sanyuankexie.attendance.common.exception.CExceptionEnum;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.concurrent.ConcurrentHashMap;

@ControllerAdvice
public class ExceptionControllerAdvice {
    Logger logger = LogFactory.getLogger(this, LogOutputType.BOTH);

    @ResponseBody
    @Order(1)
    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> customExceptionHandler(ServiceException e) {
        logger.error(false, e.getMsg());
        return ResultHelper.error(e.getCode(), e.getMsg());
    }

    @ResponseBody
    @Order(2)
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<Object> exceptionHandler(Exception e) {
        logger.error(false, e.getMessage());
        return ResultHelper.error(CExceptionEnum.UNKNOWN.getCode(), CExceptionEnum.UNKNOWN.getMsg());
    }
}
