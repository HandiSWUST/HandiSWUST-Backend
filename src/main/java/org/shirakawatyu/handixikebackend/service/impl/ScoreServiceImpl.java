package org.shirakawatyu.handixikebackend.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.api.ScoreApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.exception.NotLoginException;
import org.shirakawatyu.handixikebackend.pojo.GradePointAverage;
import org.shirakawatyu.handixikebackend.pojo.Score;
import org.shirakawatyu.handixikebackend.service.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Resource(name = "MatrixScoreApi")
    ScoreApi scoreApi;

    @Override
    public Result getScore(HttpSession session) {
        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");
        LinkedHashMap<String, ArrayList<Score>> score = scoreApi.getScore(restTemplate);
        if (score == null) {
            throw new NotLoginException();
        }
        return Result.ok().data(JSON.toJSONString(score));
    }

    @Override
    public Result getGPA(HttpSession session) {
        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");
        GradePointAverage gradePointAverage = scoreApi.getGradePointAverage(restTemplate);
        if (gradePointAverage != null) {
            JSONObject result = new JSONObject();
            result.put("gpa", gradePointAverage);
            return Result.ok().data(JSON.toJSONString(result));
        } else {
            throw new NotLoginException();
        }
    }

}