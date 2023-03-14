package org.shirakawatyu.handixikebackend.config;

import com.alibaba.fastjson.JSON;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Component
public class TimeInterceptor implements HandlerInterceptor {
    @Value(value = "${swust.close-time}")
    int closeTime;
    @Value(value = "${swust.start-time}")
    int startTime;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int hour = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
        if (hour >= closeTime && hour <= startTime) {
            response.getWriter().write(JSON.toJSONString(Result.fail().code(ResultCode.SERVER_CLOSE).msg("SERVER_CLOSE")));
            return false;
        }
        return true;
    }
}
