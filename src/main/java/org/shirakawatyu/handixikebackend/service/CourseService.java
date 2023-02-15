package org.shirakawatyu.handixikebackend.service;

import com.alibaba.fastjson2.JSONArray;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CourseService {
//    JSONArray getRawCourse();

//    JSONArray getRawCourse(RestTemplate restTemplate, long no);

    String course(HttpSession session, long no);
    String courseCurWeek(HttpSession session, long no);
    String courseSelectedWeek(HttpSession session, long no, int selectedWeek);
    String useLocalCourse(int selectedWeek, String courseData);
    String savePushData(long qq, String courseData, HttpSession session);
    String deletePushData(String studentId);
    String testPush(String studentId);
    String checkPush(String studentId);
}
