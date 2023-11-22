package org.shirakawatyu.handixikebackend.config;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.api.LoginApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * @author ShirakawaTyu
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HttpSession session = request.getSession();
        RestTemplate template = (RestTemplate) session.getAttribute("template");
        if (session.getAttribute("status") != null && template != null) {
            return true;
        } else {
            response.getWriter().write(JSON.toJSONString(Result.fail().code(ResultCode.LOGOUT)));
            return false;
        }
    }
}
