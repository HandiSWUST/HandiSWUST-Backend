package org.shirakawatyu.handixikebackend.api.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.api.LoginApi;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("CasLoginApi")
public class CasLoginApi implements LoginApi {
    private static final String keyUrl = "http://cas.swust.edu.cn/authserver/getKey";
    private static final String captchaUrl = "http://cas.swust.edu.cn/authserver/captcha";
    private static final String loginUrl = "http://cas.swust.edu.cn/authserver/login?service=http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage/cas";
    private static final String logoutUrl = "http://myo.swust.edu.cn/mht_shall/a/logout";

    @Override
    public Map<String, String> getKey(RestTemplate restTemplate) {
        ResponseEntity<String> entity = Requests.get(keyUrl, "", restTemplate);
        JSONObject key = JSON.parseObject(entity.getBody());
        Map<String, String> map = new HashMap<>();
        map.put("modulus", key.getString("modulus"));
        map.put("exponent", key.getString("exponent"));
        return map;
    }

    @Override
    public byte[] getCaptcha(RestTemplate restTemplate) {
        ResponseEntity<byte[]> entity = restTemplate.getForEntity(captchaUrl, byte[].class);
        return entity.getBody();
    }

    @Override
    public int login(String username, String password, String captcha, CookieStore cookieStore, RestTemplate restTemplate) {
        ResponseEntity<String> res = restTemplate.getForEntity(loginUrl, String.class);
        String execution;
        try {
            Document parse = Jsoup.parse(Objects.requireNonNull(res.getBody()));
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
            entity = Requests.post(loginUrl, map, restTemplate);
        } catch (HttpClientErrorException e) {
            int status = e.getStatusCode().value();
            if (cookieStore.getCookies().size() >= 3) {
                Logger.getLogger("CasLoginApi.login => ").log(Level.WARNING, "一站式大厅崩溃，但登录接口正常");
            } else if (status == 401) {
                return ResultCode.LOGIN_FAIL;
            } else {
                return ResultCode.REMOTE_SERVICE_ERROR;
            }
        }
        if (entity != null && entity.getBody() != null && entity.getBody().contains("<title>服务大厅</title>") || cookieStore.getCookies().size() >= 3) {
            return ResultCode.LOGIN_SUCCESS;
        }
        return ResultCode.LOGIN_FAIL;
    }

    @Override
    public boolean logout(RestTemplate restTemplate) {
        Requests.get(logoutUrl, "http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage", restTemplate);
        return true;
    }

    @Override
    public boolean loginCheck(RestTemplate template) {
        ResponseEntity<String> entity = Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage", "", template);
        return entity.getBody().contains("<title>服务大厅</title>");
    }
}
