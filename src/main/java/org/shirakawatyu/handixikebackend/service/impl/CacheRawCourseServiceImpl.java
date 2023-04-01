package org.shirakawatyu.handixikebackend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.common.Const;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.service.CacheRawCourseService;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.shirakawatyu.handixikebackend.utils.DateUtil;
import org.shirakawatyu.handixikebackend.utils.LessonUtils;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CacheRawCourseServiceImpl implements CacheRawCourseService {
    String[] baseUrls = {"http://sjjx.swust.edu.cn", "http://202.115.175.175"};
    int urlIndex = 1;
    @Cacheable(value = "Course", key = "'r'+#p1", unless = "null == #result")
    @Override
    public JSONArray getRawCourse(RestTemplate restTemplate, long no) {
        try {
            Requests.get(baseUrls[urlIndex] + "/swust", "", restTemplate);
        } catch (HttpClientErrorException e) {
            Requests.get(baseUrls[urlIndex] + "/swust", "", restTemplate);
        }

        Requests.get(baseUrls[urlIndex] + "/aexp/stuIndex.jsp", baseUrls[urlIndex] + "/aexp/stuLeft.jsp", restTemplate);
        Requests.get(baseUrls[urlIndex] + "/teachn/teachnAction/index.action", baseUrls[urlIndex] + "/aexp/stuLeft.jsp", restTemplate);

        // 构造表单并拿到一般课表
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("op", "getJwTimeTable");
        map.add("time", DateUtil.getCurFormatDate());
        ResponseEntity<String> entity = Requests.post(baseUrls[urlIndex] + "/teachn/stutool",map, restTemplate);

        // 转码，不然会乱码
        String lessons = new String(entity.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        JSONArray lessonsArray = null;
        try {
            lessonsArray = JSON.parseArray(lessons);
        } catch (JSONException e) {
            Logger.getLogger("At C.R.C.S.I JSONException => ").log(Level.WARNING, "错误JSON字符串：" + lessons);
            return null;
        }
        // 拿到实验课表
        // 预请求一次得到页数
        String url = baseUrls[urlIndex] + "/teachn/teachnAction/index.action?page.pageNum=2&currTeachCourseCode=&currWeek=&currYearterm=" + Const.CURRENT_TERM;
        ResponseEntity<String> preGet = Requests.get(url, baseUrls[urlIndex] + "/teachn/teachnAction/index.action", restTemplate);
        int allPage = 0;
        try {
            Document preDoc = Jsoup.parse(preGet.getBody());
            String page = preDoc.getElementById("myPage").select("p").get(0).text();
            String[] pages = page.replaceAll("页", "").replaceAll(" ", "").replaceAll("第", "").replaceAll("共", "").split("/");
            allPage = Integer.parseInt(pages[1]);
        }
        catch (Exception e) {
            Logger.getLogger("At C.R.C.S.I => ").log(Level.WARNING, "实验课表获取失败");
            throw new RuntimeException(e);
        }
        // 然后循环每一页
        for (int p = 1; p <= allPage; p++) {
            url = baseUrls[urlIndex] + "/teachn/teachnAction/index.action?page.pageNum=" + p + "&currTeachCourseCode=&currWeek=&currYearterm=" + Const.CURRENT_TERM;
            ResponseEntity<String> experiments = Requests.get(url, baseUrls[urlIndex] + "/teachn/teachnAction/index.action", restTemplate);
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
                    if(timeItems == null) {
                        Logger.getLogger("At C.R.C.S.I => ").log(Level.WARNING, String.join("-",
                                "0",
                                tds.get(4).text(),
                                tds.get(3).text(),
                                "time=null",
                                "0",
                                tds.get(1).text(),
                                "time=null",
                                "time=null",
                                "time=null",
                                "0",
                                "time=null"));
                        continue;
                    }
                    try {
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
                    }catch (Exception e) {
                        Logger.getLogger("At C.R.C.S.I Line 78 => ").log(Level.WARNING, String.join("-",
                                "0",
                                tds.get(4).text(),
                                tds.get(3).text(),
                                "time=null",
                                "0",
                                tds.get(1).text(),
                                "time=null",
                                "time=null",
                                "time=null",
                                "0",
                                "time=null"));
                    }
                }
            }
        }
        if(lessonsArray.size() > 0) {
            ArrayUtils.nullObjChk(lessonsArray);
            return lessonsArray;
        }
        return null;
    }

    @Scheduled(cron = "0 0 1 * * ? ")
    @CacheEvict(value = "Course", allEntries = true)
    @Override
    public void deleteCache() {
        Logger.getLogger("C.R.C.S.I => ").log(Level.INFO, "已清理缓存");
    }
}
