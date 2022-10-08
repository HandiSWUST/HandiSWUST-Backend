package org.shirakawatyu.handixikebackend.service;

import com.alibaba.fastjson2.JSONArray;
import org.springframework.cache.annotation.Cacheable;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CourseService {
    JSONArray getRawCourse(List<String> cookies);
    String course(List<String> cookies, HttpSession session, long no);
    String courseCurWeek(List<String> cookies, HttpSession session, long no);
    String courseSelectedWeek(List<String> cookies, HttpSession session, long no, int selectedWeek);
}
