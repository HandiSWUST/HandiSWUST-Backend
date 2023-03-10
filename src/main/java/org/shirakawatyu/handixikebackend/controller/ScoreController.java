package org.shirakawatyu.handixikebackend.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.http.client.CircularRedirectException;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.service.ScoreService;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @description: 与认证相关的接口
 * @author Alice-in-Oven
 * @date: 2022/10/2 15:00
 */
@RestController
public class ScoreController {

    @Autowired
    ScoreService scoreService;

    @GetMapping("/api/v2/extension/scores")
    @ResponseBody
    public Result scores(HttpSession session) throws CircularRedirectException {
        return scoreService.getScore(session);
    }

    @GetMapping("/api/v2/extension/gpa")
    @ResponseBody
    public Result gpa(HttpSession session) throws CircularRedirectException {
        return scoreService.getGPA(session);
    }
}
