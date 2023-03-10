package org.shirakawatyu.handixikebackend.service;

import org.shirakawatyu.handixikebackend.common.Result;

import javax.servlet.http.HttpSession;

public interface ExamService {

    public Result getExam(HttpSession session);


}
