package org.shirakawatyu.handixikebackend.api.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.api.CourseApi;
import org.shirakawatyu.handixikebackend.common.Const;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.utils.LessonUtils;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("ExperimentCourseApi")
public class ExperimentCourseApi implements CourseApi {
    private static final String baseUrl = "http://sjjx.swust.edu.cn";
    private static final String referer = baseUrl + "/teachn/teachnAction/index.action";

    @Override
    public List<Lesson> getCourse(RestTemplate restTemplate) {
        Logger logger = Logger.getLogger("ExperimentCourseApi.getCourse => ");
        // 拿到实验课表
        // 预请求一次得到页数
        ResponseEntity<String> preGet = Requests.get(getExperimentApiUrl("2", Const.CURRENT_TERM), referer, restTemplate);
        int allPage = 0;
        try {
            Document preDoc = Jsoup.parse(Objects.requireNonNull(preGet.getBody()));
            String page = Objects.requireNonNull(preDoc.getElementById("myPage")).select("p").get(0).text();
            String[] pages = page.replaceAll("页", "").replaceAll(" ", "").replaceAll("第", "").replaceAll("共", "").split("/");
            allPage = Integer.parseInt(pages[1]);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "实验课表获取失败");
            throw e;
        }

        // 然后循环每一页
        List<Lesson> lessonsArray = new ArrayList<>();
        for (int p = 1; p <= allPage; p++) {
            ResponseEntity<String> experiments = Requests.get(getExperimentApiUrl(String.valueOf(p), Const.CURRENT_TERM), referer, restTemplate);
            Document parse;
            try {
                parse = Jsoup.parse(Objects.requireNonNull(experiments.getBody()));
            }catch (Exception e) {
                logger.log(Level.SEVERE, "实验课表获取失败: " + experiments);
                return null;
            }
            Elements tabson = parse.getElementsByClass("tabson");
            Elements tbody = tabson.select("tbody");
            Elements trs = tbody.select("tr");
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() < 5) {
                    continue;
                }
                String[] timeItems = LessonUtils.timeProcess(tds.get(2).text());
                if (timeItems == null) {
                    logger.log(Level.WARNING, String.join("-", "0"
                            , tds.get(4).text(), tds.get(3).text(), "time=null", "0", tds.get(1).text(), "time=null",
                            "time=null", "time=null", "0", "time=null"));
                    continue;
                }
                try {
                    lessonsArray.add(new Lesson("0", tds.get(4).text(), tds.get(3).text(), timeItems[0],
                            "0", tds.get(1).text(), timeItems[3], timeItems[1], timeItems[4],
                            "0", timeItems[2]));
                } catch (Exception e) {
                    logger.log(Level.WARNING, String.join("-", "0",
                            tds.get(4).text(), tds.get(3).text(), "time=null", "0", tds.get(1).text(), "time=null", "time=null",
                            "time=null", "0", "time=null"));
                }
            }
        }
        return lessonsArray;
    }

    private String getExperimentApiUrl(String pageNum, String currYearterm) {
        return baseUrl + "/teachn/teachnAction/index.action?page.pageNum=" + pageNum + "&currTeachCourseCode=&currWeek=&currYearterm=" + currYearterm;
    }
}
