package org.shirakawatyu.handixikebackend.service;

import com.alibaba.fastjson2.JSONArray;
import org.springframework.cache.annotation.Cacheable;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CourseService {
    JSONArray getRawCourse();
    String course(HttpSession session, long no);
    String courseCurWeek(HttpSession session, long no);
    String courseSelectedWeek(HttpSession session, long no, int selectedWeek);
}
