package org.shirakawatyu.handixikebackend.aop;

import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
@Component
public class ApiLayerAspect {
    private final HashMap<String, Count> errorCounts = new HashMap<>();
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
        Count cnt = errorCounts.get(method);
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
            errorCounts.put(method, cnt);
        } else if (System.currentTimeMillis() > cnt.lastCountTime + CIRCLE) {
            cnt.times = 0;
            cnt.lastCountTime = System.currentTimeMillis();
        } else if (cnt.times >= THRESHOLD) {
            breakTime.put(method, System.currentTimeMillis());
            cnt.times = 0;
            cnt.lastCountTime = System.currentTimeMillis();
            Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, method + " 错误次数过多，触发熔断 " + BREAK_MILLISECOND + "ms");
            throw new CircuitBreakerException();
        }
        try {
            return point.proceed();
        } catch (ResourceAccessException e) {
            Throwable rootCause = e.getRootCause();
            if (rootCause instanceof HttpServerErrorException.GatewayTimeout |
                    rootCause instanceof SocketTimeoutException |
                    (rootCause instanceof HttpHostConnectException && rootCause.getMessage().contains("timed out")) |
                    rootCause instanceof ConnectionRequestTimeoutException) {
                cnt.times++;
                Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, "Timeout: " + method + " " + cnt.times);
                throw new CircuitBreakerException();
            } else {
                throw new RuntimeException(e);
            }
        } catch (HttpServerErrorException.BadGateway e) {
            cnt.times++;
            Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, "Bad Gateway: " + method + " " + cnt.times);
            throw new CircuitBreakerException();
        } catch (EOFException e) {
            if (e.getMessage().contains("SSL peer")) {
                cnt.times++;
                Logger.getLogger("ApiLayerAspect => ").log(Level.WARNING, "SSL peer shut down incorrectly: " + method + " " + cnt.times);
                throw new CircuitBreakerException();
            }
            throw new RuntimeException(e);
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
