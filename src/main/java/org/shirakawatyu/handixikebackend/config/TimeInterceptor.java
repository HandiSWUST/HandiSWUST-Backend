package org.shirakawatyu.handixikebackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeInterceptor implements HandlerInterceptor {
    @Value("${swust.close-time}")
    int time;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.get(Calendar.HOUR_OF_DAY) != time;
    }
}
