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

    @Autowired
    CacheRawCourseService rawCourse;

    // 不做处理返回所有课程的原值
    @Override
    public String course(HttpSession session, long no) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        if(lessonsArray.size() > 0) {
            return lessonsArray.toJSONString();
        }
        return null;
    }

    @Cacheable(value = "Course", key = "'c'+#p1", unless = "null == #result")
    @Override
    public String courseCurWeek(HttpSession session, long no) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        return LessonUtils.simpleSelectWeek(Integer.parseInt(DateUtil.curWeek()), lessonsArray);
    }

    @Cacheable(value = "Course", key = "'s'+#p2+'s'+#p1", unless = "null == #result")
    @Override
    public String courseSelectedWeek(HttpSession session, long no, int selectedWeek) {
//        throw new RuntimeException();
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        return LessonUtils.simpleSelectWeek(selectedWeek, lessonsArray);
    }

    @Override
    public String useLocalCourse(int selectedWeek, String courseData) {
        return LessonUtils.simpleSelectWeek(selectedWeek, JSONArray.parseArray(courseData));
    }
}
