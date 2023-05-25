package org.shirakawatyu.handixikebackend.service;

import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.common.Result;


public interface ExamService {

    Result getExam(HttpSession session);


}
