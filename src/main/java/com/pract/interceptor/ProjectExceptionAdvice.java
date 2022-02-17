package com.pract.interceptor;


import com.pract.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//作为异常处理器
@RestControllerAdvice   //@RestControllerAdvice可以将处理的消息以json返回，
public class ProjectExceptionAdvice {

    //拦截所有的异常消息 @RestControllerAdvice = @ControllerAdvice: @ExceptionHandler + @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex) {
        ex.printStackTrace();
        return Result.error("0", "服务器有故障，有异常抛出", null);
    }

}
