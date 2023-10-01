package org.shirakawatyu.handixikebackend.config;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Request;
import org.shirakawatyu.handixikebackend.api.LoginApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource(name = "CasLoginApi")
    LoginApi loginApi;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        RestTemplate template = (RestTemplate) session.getAttribute("template");
        try {
            return loginApi.loginCheck(template);
        } catch (Exception e) {
            if (session.getAttribute("status") == null || template == null) {
                response.getWriter().print(JSON.toJSONString(Result.ok().code(ResultCode.LOGOUT).msg("LOGOUT")));
                return false;
            }
        }
        return true;
    }
}
