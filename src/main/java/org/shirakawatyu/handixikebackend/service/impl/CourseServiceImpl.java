package org.shirakawatyu.handixikebackend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.common.Const;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.service.CourseService;
import org.shirakawatyu.handixikebackend.utils.LessonUtils;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {


    RestTemplate restTemplate;

    @Override
    public JSONArray getRawCourse(List<String> cookies) {
        Requests.get("http://202.115.175.175/swust", "", cookies, restTemplate);
        Requests.get("http://202.115.175.175/aexp/stuIndex.jsp", "http://202.115.175.175/aexp/stuLeft.jsp", cookies, restTemplate);
        Requests.get("http://202.115.175.175/teachn/teachnAction/index.action", "http://202.115.175.175/aexp/stuLeft.jsp", cookies, restTemplate);

        // 构造表单并拿到一般课表
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("op", "getJwTimeTable");
        map.add("time", Long.toString(System.currentTimeMillis()));
        ResponseEntity<String> entity = Requests.post("http://202.115.175.175/teachn/stutool", cookies, map, restTemplate);

        // 转码，不然会乱码
        String lessons = new String(entity.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        JSONArray lessonsArray = JSON.parseArray(lessons);

        // 拿到实验课表
        // 预请求一次得到页数
        String url = "http://202.115.175.175/teachn/teachnAction/index.action?page.pageNum=2&currTeachCourseCode=&currWeek=&currYearterm=" + Const.CURRENT_TERM;
        ResponseEntity<String> preGet = Requests.get(url, "http://202.115.175.175/teachn/teachnAction/index.action", cookies, restTemplate);
        int allPage = 0;
        try {
            Document preDoc = Jsoup.parse(preGet.getBody());
            String page = preDoc.getElementById("myPage").select("p").get(0).text();
            String[] pages = page.replaceAll("页", "").replaceAll(" ", "").replaceAll("第", "").replaceAll("共", "").split("/");
            allPage = Integer.parseInt(pages[1]);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 然后循环每一页
        for (int p = 1; p <= allPage; p++) {
            url = "http://202.115.175.175/teachn/teachnAction/index.action?page.pageNum=" + p + "&currTeachCourseCode=&currWeek=&currYearterm=" + Const.CURRENT_TERM;
            ResponseEntity<String> experiments = Requests.get(url, "http://202.115.175.175/teachn/teachnAction/index.action", cookies, restTemplate);
            Document parse = null;
            try {
                parse = Jsoup.parse(experiments.getBody());
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(parse != null) {
                Elements tabson = parse.getElementsByClass("tabson");
                Elements tbody = tabson.select("tbody");
                Elements trs = tbody.select("tr");
                for (int i = 0; i < trs.size(); i++) {
                    Element tr = trs.get(i);
                    Elements tds = tr.select("td");
                    if (tds.size() < 5) {
                        continue;
                    }
                    String[] timeItems = LessonUtils.timeProcess(tds.get(2).text());
                    lessonsArray.add(JSON.parseObject(JSON.toJSONString(
                            new Lesson("0",
                                    tds.get(4).text(),
                                    tds.get(3).text(),
                                    timeItems[0],
                                    "0",
                                    tds.get(1).text(),
                                    timeItems[3],
                                    timeItems[1],
                                    timeItems[4],
                                    "0",
                                    timeItems[2]))));
                }
            }
        }
        return lessonsArray;
    }

    @Cacheable(value = "Course", key = "'c'+#p2", unless = "null == #result")
    @Override
    public String course(List<String> cookies, HttpSession session, long no) {
        restTemplate = (RestTemplate) session.getAttribute("template");
        JSONArray lessonsArray = getRawCourse(cookies);
        // 对节数大于2的课以及重课处理
        LessonUtils.process(lessonsArray);

        return lessonsArray.toJSONString();
    }

    @Cacheable(value = "Course", key = "'c'+#p2", unless = "null == #result")
    @Override
    public String courseCurWeek(List<String> cookies, HttpSession session, long no) {
        restTemplate = (RestTemplate) session.getAttribute("template");
        JSONArray lessonsArray = getRawCourse(cookies);
        int size = lessonsArray.size();
        for (int i = 0; i < size; ) {
            boolean ifPlus = true;
            String week = lessonsArray.getJSONObject(i).getString("week");
            if(week.contains(",")) {
                String[] split = week.split(",");
                boolean flag = false;
                for (int j = 0; j < split.length; j++) {
                    flag = LessonUtils.isCurWeek(split[j]);
                }
                if(!flag) {
                    lessonsArray.remove(i);
                    size--;
                    ifPlus = false;
                }
            }else {
                if(!LessonUtils.isCurWeek(week)) {
                    lessonsArray.remove(i);
                    size--;
                    ifPlus = false;
                }
            }
            if(ifPlus) {
                i++;
            }
        }
        LessonUtils.process(lessonsArray);
        return lessonsArray.toJSONString();
    }
}
