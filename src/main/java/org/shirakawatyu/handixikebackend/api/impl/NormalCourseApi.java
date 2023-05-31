package org.shirakawatyu.handixikebackend.api.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import org.shirakawatyu.handixikebackend.api.CourseApi;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.utils.DateUtil;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("NormalCourseApi")
public class NormalCourseApi implements CourseApi {
    private static final String baseUrl = "http://sjjx.swust.edu.cn";
    @Override
    public List<Lesson> getCourse(RestTemplate restTemplate) {
        Requests.get(baseUrl + "/swust", "", restTemplate);
        Requests.get(baseUrl + "/aexp/stuIndex.jsp", baseUrl + "/aexp/stuLeft.jsp", restTemplate);
        Requests.get(baseUrl + "/teachn/teachnAction/index.action", baseUrl + "/aexp/stuLeft.jsp", restTemplate);

        // 构造表单并拿到一般课表
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("op", "getJwTimeTable");
        map.add("time", DateUtil.getCurFormatDate());
        ResponseEntity<String> entity = Requests.post(baseUrl + "/teachn/stutool", map, restTemplate);

        // 转码，不然会乱码
        String lessons = new String(entity.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        List<Lesson> lessonsArray;
        try {
            lessonsArray = JSON.parseArray(lessons, Lesson.class);
        } catch (JSONException e) {
            Logger.getLogger("At NormalCourseApi JSONException => ").log(Level.WARNING, "错误JSON字符串：" + lessons);
            return null;
        }
        return lessonsArray;
    }
}
