package org.shirakawatyu.handixikebackend.service;

import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.common.Result;


/**
 * @author ShirakawaTyu
 */
public interface CourseService {
    Result course(HttpSession session, String no);
    Result courseCurWeek(HttpSession session, String no);
    Result courseSelectedWeek(HttpSession session, String no, int selectedWeek);
    Result useLocalCourse(int selectedWeek, String courseData);
    Result manualDeleteCache(String no);
}
