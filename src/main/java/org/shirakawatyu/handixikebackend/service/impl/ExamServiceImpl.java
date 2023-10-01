package org.shirakawatyu.handixikebackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.shirakawatyu.handixikebackend.api.ExamApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.pojo.Exam;
import org.shirakawatyu.handixikebackend.service.ExamService;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class ExamServiceImpl implements ExamService {

    @Resource(name = "examApi")
    ExamApi examApi;


    @Override
    public Result getExam(HttpSession session) {
        RestTemplate restTemplate = Requests.getRestTemplate(session);
        String exam = examApi.getExam(restTemplate);
        if(JSONUtil.isTypeJSON(exam)) {
            return Result.ok().data(exam);
        }
        return Result.fail().msg(exam);
    }
}
