package org.shirakawatyu.handixikebackend.service;

import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.common.Result;


public interface CourseService {
    Result course(HttpSession session, long no);
    Result courseCurWeek(HttpSession session, long no);
    Result courseSelectedWeek(HttpSession session, long no, int selectedWeek);
    Result useLocalCourse(int selectedWeek, String courseData);
}
