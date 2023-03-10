package org.shirakawatyu.handixikebackend.service;

import com.alibaba.fastjson2.JSONArray;
import org.shirakawatyu.handixikebackend.common.Result;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CourseService {
    Result course(HttpSession session, long no);
    Result courseCurWeek(HttpSession session, long no);
    Result courseSelectedWeek(HttpSession session, long no, int selectedWeek);
    Result useLocalCourse(int selectedWeek, String courseData);
}
