package org.shirakawatyu.handixikebackend.controller;


import org.shirakawatyu.handixikebackend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpSession;
import java.util.*;
/**
 * @description: 与认证相关的接口
 * @author ShirakawaTyu
 * @date: 2022/10/1 17:40
 */

@Controller
@Scope("prototype")
public class LoginController {
    @Autowired

    LoginService loginService;

    @GetMapping("/api/key")
    @ResponseBody

    public Map<String,String> getKey(HttpSession session) {
        return loginService.getKey(session);
    }

    @GetMapping("/api/captcha")
    @ResponseBody
    public String getCaptcha(HttpSession session) {

        return loginService.getCaptcha(session);
    }

    @PostMapping("/api/login")
    @ResponseBody
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("captcha") String captcha, HttpSession session) {

        return loginService.login(username, password, captcha, session);
    }

    @GetMapping("/api/logout")
    @ResponseBody
    public String logout(HttpSession session) {

        return loginService.logout(session);
    }

    @GetMapping("/api/loginCheck")
    @ResponseBody
    public String loginCheck(HttpSession session) {

        return loginService.loginCheck(session);


    }
}