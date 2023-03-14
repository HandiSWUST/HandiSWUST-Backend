package org.shirakawatyu.handixikebackend.config;

import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

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
            if (element.getClassName().contains("ServiceImpl")) {
                classLocation.append(element.getClassName())
                        .append(": ")
                        .append(element.getLineNumber());
                break;
            }
        }
        Logger.getLogger("GlobalExceptionHandler")
                .log(Level.WARNING, "Timeout: " + classLocation);
        return Result.fail().code(ResultCode.TIMEOUT).msg("TIMEOUT");
    }

    @ExceptionHandler(value = HttpServerErrorException.InternalServerError.class)
    public Result HttpServerErrorExceptionHandler(HttpServerErrorException e) {
        HttpHeaders responseHeaders = e.getResponseHeaders();
        if (responseHeaders != null && responseHeaders.getHost() != null) {
            Logger.getLogger("GlobalExceptionHandler")
                    .log(Level.WARNING, "500 Internal Server Error: " + responseHeaders.getHost().getHostName());
        }
        return Result.fail().code(ResultCode.REMOTE_SERVICE_ERROR).msg("REMOTE_SERVICE_ERROR");
    }
}
