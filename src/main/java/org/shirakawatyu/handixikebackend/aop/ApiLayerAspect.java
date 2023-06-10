package org.shirakawatyu.handixikebackend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
@Component
public class ApiLayerAspect {
    private final HashMap<String, Integer> timeoutCounts = new HashMap<>();
    private final HashMap<String, Long> breakTime = new HashMap<>();
    private final int THRESHOLD = 10;
    private final long BREAK_MILLISECOND = 60000;
    @Pointcut("execution(* org.shirakawatyu.handixikebackend.api.impl.*.*(..))")
    public void exception() {}

    @Around("exception()")
    public Object around(ProceedingJoinPoint point) {
        String method = point.getSignature().toShortString();
        Integer throwTimes = timeoutCounts.get(method);
        Long time = breakTime.get(method);
        if (time != null) {
            if (System.currentTimeMillis() - time < BREAK_MILLISECOND) {
                throw new CircuitBreakerException();
            } else {
                breakTime.remove(method);
            }
        }
        if (throwTimes == null) {
            timeoutCounts.put(method, 0);
            throwTimes = 0;
        } else if (throwTimes >= THRESHOLD) {
            breakTime.put(method, System.currentTimeMillis());
            timeoutCounts.put(method, 0);
            Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, method + " 超时次数过多，触发熔断 " + BREAK_MILLISECOND + "ms");
            throw new CircuitBreakerException();
        }
        try {
            return point.proceed();
        } catch (ResourceAccessException e) {
            Throwable rootCause = e.getRootCause();
            if (rootCause instanceof HttpServerErrorException.GatewayTimeout |
                    rootCause instanceof SocketTimeoutException) {
                throwTimes++;
                timeoutCounts.put(method, throwTimes);
                Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, "Timeout: " + method);
                throw new CircuitBreakerException();
            } else {
                throw new RuntimeException(e);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static class CircuitBreakerException extends RuntimeException {}
}
