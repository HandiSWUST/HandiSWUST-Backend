package org.shirakawatyu.handixikebackend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.cache.RawCourseCache;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.service.CourseService;
import org.shirakawatyu.handixikebackend.utils.DateUtil;
import org.shirakawatyu.handixikebackend.utils.LessonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    RawCourseCache rawCourse;

    // 不做处理返回所有课程的原值
    @Override
    public Result course(HttpSession session, long no) {
        List<Lesson> lessonList = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        JSONArray lessonsArray = new JSONArray(lessonList);
        if(lessonsArray.size() > 0) {
            return Result.ok().data(lessonsArray.toJSONString());
        }
        return Result.fail();
    }

    @Cacheable(value = "Course", key = "'c'+#p1", unless = "#result.data eq '[]'")
    @Override
    public Result courseCurWeek(HttpSession session, long no) {
        List<Lesson> lessonList = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        String s = LessonUtils.simpleSelectWeek(Integer.parseInt(DateUtil.curWeek()), lessonList);
        return Result.ok().data(s);
    }

    @Cacheable(value = "Course", key = "'s'+#p2+'s'+#p1", unless = "#result.data eq '[]'")
    @Override
    public Result courseSelectedWeek(HttpSession session, long no, int selectedWeek) {
        List<Lesson> lessonList = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        String s = LessonUtils.simpleSelectWeek(selectedWeek, lessonList);
        return Result.ok().data(s);
    }

    @Override
    public Result useLocalCourse(int selectedWeek, String courseData) {
        return Result.ok().data(LessonUtils.simpleSelectWeek(selectedWeek, JSONArray.parseArray(courseData, Lesson.class)));
    }

}
