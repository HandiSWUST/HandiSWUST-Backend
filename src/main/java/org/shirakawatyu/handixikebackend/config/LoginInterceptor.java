package org.shirakawatyu.handixikebackend.config;

import com.alibaba.fastjson.JSON;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if(session.getAttribute("status") == null) {
            response.getWriter().print(JSON.toJSONString(Result.ok().code(ResultCode.LOGOUT).msg("LOGOUT")));
            return false;
        }
        return true;
    }
}
