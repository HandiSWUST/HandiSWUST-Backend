package org.shirakawatyu.handixikebackend.service.impl;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.shirakawatyu.handixikebackend.api.LoginApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.config.InitRestTemplate;
import org.shirakawatyu.handixikebackend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource(name="CasLoginApi")
    LoginApi casLoginApi;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public Result getKey(HttpSession session) {
        RestTemplate restTemplate =(RestTemplate)session.getAttribute("template");
        if (restTemplate == null) {
            return null;
        }
        Map<String, String> key = casLoginApi.getKey(restTemplate);
        return Result.ok().data(key);
    }

    @Override
    public Result getCaptcha(HttpSession session) {
        BasicCookieStore cookieStore = new BasicCookieStore();
        RestTemplate restTemplate = InitRestTemplate.init(cookieStore);
        session.setAttribute("template",restTemplate);
        session.setAttribute("cookieStore",cookieStore);
        byte[] captcha = casLoginApi.getCaptcha(restTemplate);
        return Result.ok().data(Base64.getEncoder().encodeToString(captcha));
    }


    @Override
    public Result login(String username, String password, String captcha, HttpSession session) {
        RestTemplate restTemplate =(RestTemplate)session.getAttribute("template");
        CookieStore cookieStore = (CookieStore)session.getAttribute("cookieStore");
        if(restTemplate == null) {
            return null;
        }
        int result = casLoginApi.login(username, password, captcha, cookieStore, restTemplate);
        session.setAttribute("template", restTemplate);
        if (result == ResultCode.LOGIN_SUCCESS) {
            session.setAttribute("status", true);
            session.setAttribute("cookieStore", cookieStore);
            session.setAttribute("no", username);
            // 统计每日登录人次
            redisTemplate.opsForHash().increment("count", new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 1);
            // 登录人数
            redisTemplate.opsForHyperLogLog().add("DAU:" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), username);
            return Result.ok().code(ResultCode.LOGIN_SUCCESS).msg("LOGIN SUCCESS");
        } else if (result == ResultCode.REMOTE_SERVICE_ERROR) {
            Result.fail().code(ResultCode.REMOTE_SERVICE_ERROR).msg("REMOTE SERVICE ERROR");
        }
        return Result.fail().code(ResultCode.LOGIN_FAIL).msg("LOGIN FAIL");
    }

    @Override
    public Result logout(HttpSession session) {
        RestTemplate restTemplate =(RestTemplate)session.getAttribute("template");
        casLoginApi.logout(restTemplate);
        session.removeAttribute("status");
        session.removeAttribute("template");
        session.removeAttribute("cookieStore");
        return Result.ok().code(ResultCode.LOGOUT_SUCCESS).msg("LOGOUT SUCCESS");
    }


    public Result loginCheck(HttpSession session) {
        if (session.getAttribute("status") == null) {
            return Result.ok().code(ResultCode.LOGOUT).msg("LOGOUT");
        }
        return Result.ok().code(ResultCode.HAS_LOGIN).msg("LOGIN");
    }
}
