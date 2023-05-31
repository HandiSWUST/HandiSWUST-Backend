package org.shirakawatyu.handixikebackend.controller;

import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.service.impl.ExamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ExamController {

    @Autowired
    ExamServiceImpl examService;

    @RequestMapping("/api/v2/extension/getExam")
    public Result getExam(HttpSession session){
        return examService.getExam(session);

    }
}
