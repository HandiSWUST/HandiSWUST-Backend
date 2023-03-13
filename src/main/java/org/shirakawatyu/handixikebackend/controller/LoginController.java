package org.shirakawatyu.handixikebackend.controller;


import cn.hutool.crypto.asymmetric.RSA;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.*;
/**
 * @description: 与认证相关的接口
 * @author ShirakawaTyu
 * @date: 2022/10/1 17:40
 */

@RestController
@RequestMapping("/api/v2/login")
public class LoginController {
    @Autowired

    LoginService loginService;

    @GetMapping("/key")
    public Result getKey(HttpSession session) {
        return loginService.getKey(session);
    }

    @GetMapping("/captcha")
    public Result getCaptcha(HttpSession session) {
        return loginService.getCaptcha(session);
    }

    @PostMapping("/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("captcha") String captcha, HttpSession session) {
        return loginService.login(username, password, captcha, session);
    }

    @GetMapping("/logout")
    public Result logout(HttpSession session) {
        return loginService.logout(session);
    }

    @GetMapping("/loginCheck")
    public Result loginCheck(HttpSession session) {
        return loginService.loginCheck(session);
    }
}