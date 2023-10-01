package org.shirakawatyu.handixikebackend.api;

import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public interface LoginApi {
    Map<String, String> getKey(RestTemplate restTemplate);
    byte[] getCaptcha(RestTemplate restTemplate);
    int login(String username, String password, String captcha, CookieStore cookieStore, RestTemplate restTemplate);
    boolean logout(RestTemplate restTemplate);
    boolean loginCheck(RestTemplate template);
}
