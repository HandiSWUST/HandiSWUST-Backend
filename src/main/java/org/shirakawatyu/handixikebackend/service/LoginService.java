package org.shirakawatyu.handixikebackend.service;

import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.common.Result;


public interface LoginService {
    Result getKey(HttpSession session);
    Result getCaptcha(HttpSession session);
    Result login(String username, String password, String captcha, HttpSession session);
    Result logout(HttpSession session);
    Result loginCheck(HttpSession session);
}
