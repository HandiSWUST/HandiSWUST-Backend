package org.shirakawatyu.handixikebackend.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.api.CourseApi;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.utils.LessonUtils;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.FutureTask;

/**
 * @author ShirakawaTyu
 */
@Component("ExperimentCourseApi")
@Slf4j
public class ExperimentCourseApi implements CourseApi {
    private static final String BASE_URL = "https://sjjx.dean.swust.edu.cn";
    private static final String REFERER = BASE_URL + "/teachn/teachnAction/index.action";

    @Override
    public List<Lesson> getCourse(CookieStore cookieStore) {
        Requests.getForBytes(BASE_URL + "/swust", "", cookieStore);
        // 拿到实验课表
        // 预请求一次得到页数
        String body = Requests.getForString(getExperimentApiUrl("1"), REFERER, cookieStore);
        int allPage;
        try {
            Document preDoc = Jsoup.parse(Objects.requireNonNull(body));
            String page = Objects.requireNonNull(preDoc.getElementById("myPage")).select("p").get(0).text();
            String[] pages = page.replaceAll("页", "").replaceAll(" ", "").replaceAll("第", "").replaceAll("共", "").split("/");
            allPage = Integer.parseInt(pages[1]);
        } catch (Exception e) {
            log.error("实验课表获取失败: " + body);
            throw e;
        }
        // 然后循环每一页
        List<Lesson> lessonsArray = new ArrayList<>();
        List<FutureTask<String>> pages = getPages(allPage, cookieStore);
        for (FutureTask<String> page: pages) {
            Document parse;
            try {
                body = page.get();
                parse = Jsoup.parse(Objects.requireNonNull(body));
            } catch (Exception e) {
                log.error("实验课表获取失败: " + body);
                throw new RuntimeException(e.getCause());
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
                    log.warn(String.join("-", "0", tds.get(4).text(), tds.get(3).text(), "time=null",
                            "0", tds.get(1).text(), "time=null", "time=null", "time=null", "0", "time=null"));
                    continue;
                }
                try {
                    lessonsArray.add(new Lesson(tds.get(0).text(), tds.get(4).text(), tds.get(3).text(), timeItems[0],
                            "0", tds.get(1).text(), timeItems[3], timeItems[1], timeItems[4],
                            "0", timeItems[2]));
                } catch (Exception e) {
                    log.warn(String.join("-", "0", tds.get(4).text(), tds.get(3).text(), "time=null",
                            "0", tds.get(1).text(), "time=null", "time=null", "time=null", "0", "time=null"));
                }
            }
        }
        return lessonsArray;
    }

    private String getExperimentApiUrl(String pageNum) {
        return BASE_URL + "/teachn/teachnAction/index.action?page.pageNum=" + pageNum;
    }

    private List<FutureTask<String>> getPages(int totalPage, CookieStore cookieStore) {
        List<FutureTask<String>> futureTasks = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            int finalI = i;
            FutureTask<String> futureTask = new FutureTask<>(() ->
                Requests.getForString(getExperimentApiUrl(String.valueOf(finalI)), REFERER, cookieStore));
            futureTasks.add(futureTask);
            Thread.startVirtualThread(futureTask);
        }
        return futureTasks;
    }
}
