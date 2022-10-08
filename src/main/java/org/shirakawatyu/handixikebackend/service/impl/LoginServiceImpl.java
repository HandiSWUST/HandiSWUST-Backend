package org.shirakawatyu.handixikebackend.service.impl;

import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.config.InitRestTemplate;
import org.shirakawatyu.handixikebackend.service.LoginService;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    RestTemplate restTemplate;
    BasicCookieStore cookieStore;

    int count = 1;

    @Override
    public Map<String, String> getKey(HttpSession session) {
//        List<String> cookies = ArrayUtils.arrayToList((Object[]) session.getAttribute("cookies"));
        if(this.restTemplate == null) {
            return null;
        }
        ResponseEntity<String> entity = Requests.get("http://cas.swust.edu.cn/authserver/getKey", "", restTemplate);
//        session.setAttribute("cookies", cookies.toArray());
        String text = entity.toString();
        Map<String, String> map = new HashMap<>();
        map.put("modulus", text.substring(17, text.indexOf("\",")));
        map.put("exponent", text.substring(text.indexOf("\"exponent\":\"")+12, text.indexOf("\"},")));
        return map;
    }

    @Override
    public String getCaptcha(HttpSession session) {
        cookieStore = new BasicCookieStore();
        restTemplate = InitRestTemplate.init(cookieStore);
        ResponseEntity<byte[]> entity = restTemplate.getForEntity("http://cas.swust.edu.cn/authserver/captcha", byte[].class);
//        if(entity.getHeaders().get("Set-Cookie") != null) {
//            List<String> cookies = entity.getHeaders().get("Set-Cookie");
//            session.setAttribute("cookies", cookies.toArray());
//        }
        byte[] bytes = entity.getBody();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 登录认证后将cookies放入session，attribute名字为cookies，取用时调用 ArrayUtils.arrayToList((Object[]) session.getAttribute("cookies"); 即可
     * 返回cookies列表
     *
     * @param username
     * @param password
     * @param captcha
     * @param session
     * @return
     */
    @Override
    public String login(String username, String password, String captcha, HttpSession session) {
        if(this.restTemplate == null) {
            return null;
        }
//        List<String> cookies = ArrayUtils.arrayToList((Object[]) session.getAttribute("cookies"));
        ResponseEntity<String> res = restTemplate.getForEntity("http://cas.swust.edu.cn/authserver/login?service=http://202.115.175.175/swust/", String.class);
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
            entity = Requests.post("http://cas.swust.edu.cn/authserver/login?service=http://202.115.175.175/swust/", map, restTemplate);
        }catch (Exception e) {
            return "1500 LOGIN FAIL";
        }
//        session.setAttribute("cookies", cookies.toArray());
        session.setAttribute("template", restTemplate);
        count++;

        if(entity != null && entity.getBody() != null && entity.getBody().contains("西南科技大学学生实践教学自助学习系统")) {
            session.setAttribute("status", true);
            session.setAttribute("cookieStore", cookieStore);
            session.setAttribute("no", username);
            return "1200 LOGIN SUCCESS";
        }
        return "1500 LOGIN FAIL";
    }

    @Override
    public String logout(HttpSession session) {
//        List<String> cookies = ArrayUtils.arrayToList((Object[]) session.getAttribute("cookies"));
        Requests.get("http://myo.swust.edu.cn/mht_shall/a/logout", "http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage", restTemplate);
//        session.removeAttribute("cookies");
        session.removeAttribute("status");
        session.removeAttribute("template");
        session.removeAttribute("cookieStore");
        return "2200 LOGOUT SUCCESS";
    }


    public String loginCheck(HttpSession session) {
        if(session.getAttribute("status") == null) return "3401 LOGOUT";
        return "3200 LOGIN";
    }
}
