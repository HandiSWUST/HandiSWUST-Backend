package org.shirakawatyu.handixikebackend.controller;

import org.shirakawatyu.handixikebackend.service.impl.ExamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class ExamController {

    @Autowired
    ExamServiceImpl examService;

    @RequestMapping("/api/getExam")
    public String getExam(HttpSession session){
        return examService.getExam(session);

    }
}
