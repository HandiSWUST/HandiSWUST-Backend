package org.shirakawatyu.handixikebackend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import org.shirakawatyu.handixikebackend.service.CacheRawCourseService;
import org.shirakawatyu.handixikebackend.service.CourseService;
import org.shirakawatyu.handixikebackend.utils.DateUtil;
import org.shirakawatyu.handixikebackend.utils.LessonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@Service
public class CourseServiceImpl implements CourseService {

//    RestTemplate restTemplate;
    @Autowired
    CacheRawCourseService rawCourse;


    @Cacheable(value = "Course", key = "'a'+#p1", unless = "null == #result")
    @Override
    public String course(HttpSession session, long no) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        // 对节数大于2的课以及重课处理
        LessonUtils.process(lessonsArray);
        if(lessonsArray.size() > 0) {
            return lessonsArray.toJSONString();
        }
        return null;
    }

    @Cacheable(value = "Course", key = "'c'+#p1", unless = "null == #result")
    @Override
    public String courseCurWeek(HttpSession session, long no) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        LessonUtils.onlySelectWeek(Integer.parseInt(DateUtil.curWeek()), lessonsArray);
        LessonUtils.process(lessonsArray);
        if(lessonsArray.size() > 0) {
            return lessonsArray.toJSONString();
        }
        return null;
    }

    @Cacheable(value = "Course", key = "'s'+#p2+'s'+#p1", unless = "null == #result")
    @Override
    public String courseSelectedWeek(HttpSession session, long no, int selectedWeek) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        LessonUtils.onlySelectWeek(selectedWeek, lessonsArray);
        LessonUtils.process(lessonsArray);
        if(lessonsArray.size() > 0) {
            return lessonsArray.toJSONString();
        }
        return null;
    }
}
