package org.shirakawatyu.handixikebackend.service.impl;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.config.InitRestTemplate;
import org.shirakawatyu.handixikebackend.service.LoginService;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service

public class LoginServiceImpl implements LoginService {


    @Autowired
    StringRedisTemplate redisTemplate;
    int count = 1;

    @Override
    public Result getKey(HttpSession session) {
        RestTemplate restTemplate =(RestTemplate)session.getAttribute("template");
        if(restTemplate == null) {
            return null;
        }
        ResponseEntity<String> entity = Requests.get("http://cas.swust.edu.cn/authserver/getKey", "", restTemplate);
        String text = entity.toString();
        Map<String, String> map = new HashMap<>();
        map.put("modulus", text.substring(17, text.indexOf("\",")));
        map.put("exponent", text.substring(text.indexOf("\"exponent\":\"")+12, text.indexOf("\"},")));
        return Result.ok().data(map);
    }

    @Override
    public Result getCaptcha(HttpSession session) {
        BasicCookieStore cookieStore = new BasicCookieStore();

        RestTemplate restTemplate = InitRestTemplate.init(cookieStore);
        session.setAttribute("template",restTemplate);
        session.setAttribute("cookieStore",cookieStore);
        ResponseEntity<byte[]> entity = restTemplate.getForEntity("http://cas.swust.edu.cn/authserver/captcha", byte[].class);
        byte[] bytes = entity.getBody();
        return Result.ok().data(Base64.getEncoder().encodeToString(bytes));
    }


    @Override
    public Result login(String username, String password, String captcha, HttpSession session) {
        RestTemplate restTemplate =(RestTemplate)session.getAttribute("template");
        CookieStore cookieStore = (CookieStore)session.getAttribute("cookieStore");
        if(restTemplate == null) {
            return null;
        }
        ResponseEntity<String> res = restTemplate.getForEntity("http://cas.swust.edu.cn/authserver/login", String.class);
        String execution = null;
        try {
            Document parse = Jsoup.parse(res.getBody());
            Elements formCont = parse.getElementsByAttributeValue("name", "execution");
            execution = formCont.get(0).attr("value");
        }catch (Exception e) {
            execution = "e1s1";
        }

        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("execution", execution);
        map.add("_eventId", "submit");
        map.add("geolocation", "");
        map.add("username", username);
        map.add("lm", "usernameLogin");
        map.add("password", password);
        map.add("captcha", captcha);

        ResponseEntity<String> entity = null;
        try {
            entity = Requests.post("http://cas.swust.edu.cn/authserver/login", map, restTemplate);
        }catch (HttpClientErrorException e) {
            int status = e.getRawStatusCode();
            if (cookieStore.getCookies().size() >= 3) {
                Logger.getLogger("o.s.h.s.i.LoginServiceImpl").log(Level.WARNING, "一站式大厅崩溃，但登录接口正常");
            } else if (status == 401) {
                return Result.fail().code(ResultCode.LOGIN_FAIL).msg("LOGIN FAIL");
            }
            else {
                return Result.fail().code(ResultCode.REMOTE_SERVICE_ERROR).msg("REMOTE SERVICE ERROR");
            }
        }
        session.setAttribute("template", restTemplate);
        count++;

        // 临时改动，过后记得改回来
        if(entity != null && entity.getBody() != null && entity.getBody().contains("location.href = '/sys/portal/page.jsp';") || cookieStore.getCookies().size() >= 3) {
            session.setAttribute("status", true);
            session.setAttribute("cookieStore", cookieStore);
            session.setAttribute("no", username);
            // 统计每日登录人次
            redisTemplate.opsForHash().increment("count", new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 1);
            // 日活统计
            redisTemplate.opsForHyperLogLog().add("DAU:" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), username);
            return Result.ok().code(ResultCode.LOGIN_SUCCESS).msg("LOGIN SUCCESS");
        }
        return Result.fail().code(ResultCode.LOGIN_FAIL).msg("LOGIN FAIL");
    }

    @Override
    public Result logout(HttpSession session) {
        RestTemplate restTemplate =(RestTemplate)session.getAttribute("template");
        Requests.get("http://myo.swust.edu.cn/mht_shall/a/logout", "http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage", restTemplate);
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
