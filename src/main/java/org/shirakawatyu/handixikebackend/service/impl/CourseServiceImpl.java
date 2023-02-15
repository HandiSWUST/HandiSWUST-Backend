package org.shirakawatyu.handixikebackend.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import org.apache.http.impl.client.BasicCookieStore;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CacheRawCourseService rawCourse;
    @Value("${push.url}")
    String pushUrl;
    @Value("${push.signature}")
    String signature;

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

    @Override
    public String savePushData(long qq, String courseData, HttpSession session) {
        String decodeData = URLDecoder.decode(courseData, StandardCharsets.UTF_8);
        long no = Long.parseLong((String) session.getAttribute("no"));
        LessonMessage lessonMessage = new LessonMessage(no, qq, JSONArray.parseArray(decodeData));
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(JSON.toJSONString(lessonMessage).getBytes(StandardCharsets.UTF_8));
        try {
            InitRestTemplate.init(new BasicCookieStore()).postForObject(pushUrl + "/api/push/save?sign=" + SignUtil.getSign(signature), requestEntity, String.class);
        }catch (Exception e) {
            e.printStackTrace();
            return "5501 PUSHING SERVICE ERROR";
        }
        return "5200 SUCCESS";
    }

    @Override
    public String deletePushData(String studentId) {
        try {
            InitRestTemplate.init(new BasicCookieStore()).delete(pushUrl + "/api/push/delete/" + studentId + "?sign=" + SignUtil.getSign(signature));
        }catch (Exception e) {
            e.printStackTrace();
            return "5502 DELETE FAIL";
        }
        return "5200 SUCCESS";
    }

    @Override
    public String testPush(String studentId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sign", SignUtil.getSign(signature));
        String s = HttpUtil.get(pushUrl + "/api/push/test/" + studentId, map);
        if (s.equals("SUCCESS")) {
            return "5200 SUCCESS";
        } else {
            return "5500 FAIL";
        }
    }

    @Override
    public String checkPush(String studentId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sign", SignUtil.getSign(signature));
        String s = HttpUtil.get(pushUrl + "/api/push/check/" + studentId, map);
        if (s.equals("EXIST")) {
            return "5200 SUCCESS";
        } else {
            return "5500 FAIL";
        }
    }

}
