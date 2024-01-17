package com.nobody.nobodyplace.handler;

import com.nobody.nobodyplace.exception.BaseException;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.old.CommonStorageService;
import com.nobody.nobodyplace.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger Nlog = LoggerFactory.getLogger(CommonStorageService.class);


    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        String message = ex.getMessage();
        Nlog.error("Exception：{}", message);
        if (message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + Constant.MSG_ALREADY_EXISTS;
            return Result.error(msg);
        } else {
            return Result.error(Constant.MSG_OTHERS);
        }
    }
}
