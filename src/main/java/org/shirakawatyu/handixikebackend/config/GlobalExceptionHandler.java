package org.shirakawatyu.handixikebackend.config;

import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SocketTimeoutException.class)
    public Result SocketTimeoutExceptionHandler(SocketTimeoutException ste) {
        StackTraceElement[] stackTrace = ste.getStackTrace();
        StringBuilder classLocation = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains("shirakawatyu")) {
                classLocation.append(element.getClassName()).append("\n");
            }
        }
        Logger.getLogger("GlobalExceptionHandler")
                .log(Level.WARNING, "请求超时，位于" + classLocation);
        return Result.fail().code(ResultCode.TIMEOUT).msg("TIMEOUT");
    }
}
