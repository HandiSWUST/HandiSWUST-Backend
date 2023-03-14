package org.shirakawatyu.handixikebackend.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import org.apache.http.impl.client.BasicCookieStore;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.config.InitRestTemplate;
import org.shirakawatyu.handixikebackend.pojo.LessonMessage;
import org.shirakawatyu.handixikebackend.service.CacheRawCourseService;
import org.shirakawatyu.handixikebackend.service.CourseService;
import org.shirakawatyu.handixikebackend.utils.DateUtil;
import org.shirakawatyu.handixikebackend.utils.LessonUtils;
import org.shirakawatyu.handixikebackend.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.shirakawatyu.handixikebackend.common.Const.CURRENT_TERM_LONG;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CacheRawCourseService rawCourse;

    // 不做处理返回所有课程的原值
    @Override
    public Result course(HttpSession session, long no) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        if(lessonsArray.size() > 0) {
            return Result.ok().data(lessonsArray.toJSONString());
        }
        return Result.fail();
    }

    @Cacheable(value = "Course", key = "'c'+#p1", unless = "null == #result")
    @Override
    public Result courseCurWeek(HttpSession session, long no) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        String s = LessonUtils.simpleSelectWeek(Integer.parseInt(DateUtil.curWeek()), lessonsArray);
        if (s.equals("[]")) {
            return null;
        }
        return Result.ok().data(s);
    }

    @Cacheable(value = "Course", key = "'s'+#p2+'s'+#p1", unless = "null == #result")
    @Override
    public Result courseSelectedWeek(HttpSession session, long no, int selectedWeek) {
        JSONArray lessonsArray = rawCourse.getRawCourse((RestTemplate) session.getAttribute("template"), no);
        String s = LessonUtils.simpleSelectWeek(selectedWeek, lessonsArray);
        if (s.equals("[]")) {
            return null;
        }
        return Result.ok().data(s);
    }

    @Override
    public Result useLocalCourse(int selectedWeek, String courseData) {
        return Result.ok().data(LessonUtils.simpleSelectWeek(selectedWeek, JSONArray.parseArray(courseData)));
    }

}
