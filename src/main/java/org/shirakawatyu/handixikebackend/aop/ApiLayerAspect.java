package org.shirakawatyu.handixikebackend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
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
    private final HashMap<String, Count> timeoutCounts = new HashMap<>();
    private final HashMap<String, Long> breakTime = new HashMap<>();
    @Value("${swust.api.breaker.threshold:20}")
    private int THRESHOLD;    // 超时阈值，单位：次
    @Value("${swust.api.breaker.break-millisecond:120000}")
    private long BREAK_MILLISECOND;    // 熔断时间，单位：ms
    @Value("${swust.api.breaker.circle:60000}")
    private long CIRCLE;    // 统计周期，单位：ms
    @Pointcut("execution(* org.shirakawatyu.handixikebackend.api.impl.*.*(..))")
    public void exception() {}

    @Around("exception()")
    public Object around(ProceedingJoinPoint point) {
        String method = point.getSignature().toShortString();
        Count cnt = timeoutCounts.get(method);
        Long time = breakTime.get(method);
        if (time != null) {
            if (System.currentTimeMillis() - time < BREAK_MILLISECOND) {
                throw new CircuitBreakerException();
            } else {
                breakTime.remove(method);
            }
        }
        if (cnt == null) {
            cnt = new Count(0, System.currentTimeMillis());
            timeoutCounts.put(method, cnt);
        } else if (System.currentTimeMillis() > cnt.lastCountTime + CIRCLE) {
            cnt.times = 0;
            cnt.lastCountTime = System.currentTimeMillis();
        } else if (cnt.times >= THRESHOLD) {
            breakTime.put(method, System.currentTimeMillis());
            cnt.times = 0;
            cnt.lastCountTime = System.currentTimeMillis();
            Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, method + " 超时次数过多，触发熔断 " + BREAK_MILLISECOND + "ms");
            throw new CircuitBreakerException();
        }
        try {
            return point.proceed();
        } catch (ResourceAccessException e) {
            Throwable rootCause = e.getRootCause();
            if (rootCause instanceof HttpServerErrorException.GatewayTimeout |
                    rootCause instanceof SocketTimeoutException) {
                cnt.times++;
                Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, "Timeout: " + method + " " + cnt.times);
                throw new CircuitBreakerException();
            } else {
                throw new RuntimeException(e);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static class CircuitBreakerException extends RuntimeException {}

    static class Count {
        int times;
        long lastCountTime;

        public Count(int times, long lastCountTime) {
            this.times = times;
            this.lastCountTime = lastCountTime;
        }
    }
}
