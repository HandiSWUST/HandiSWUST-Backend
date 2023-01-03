package org.shirakawatyu.handixikebackend;

import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.shirakawatyu.handixikebackend.common.Const;
import org.shirakawatyu.handixikebackend.pojo.Score;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.shirakawatyu.handixikebackend.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootTest
class HandiXikeBackendApplicationTests {

    @Test
    public void getScore() {
//        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");
        List<String> scores = new ArrayList<>();
        try {
//            Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
//            ResponseEntity<String> responseEntity1 = Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentProfile:courseMark", "", restTemplate);
//            scores = Jsoup.parse(responseEntity1.getBody()).getElementsByClass("UItable").select("tr").eachText();
            scores.add("2022-2023 学年 共计 1 门课程");
            scores.add("秋");
            scores.add("课程 课程号 学分 课程性质 正考 补考 绩点");
            scores.add("大学生心理健康教育 FX160020 1.0 必修 95.0 4.50");
            scores.add("平均学分绩点 4.500 必修课绩点 4.500");
            System.out.println(JSON.toJSONString(processScore(scores)));
        }catch (Exception e) {
            e.printStackTrace();
            if (scores != null) Logger.getLogger("S.S.I").log(Level.WARNING, scores.toString());
//            return "3401 LOGOUT";
        }
    }

    private LinkedHashMap<String, ArrayList<Object>> processScore(List<String> scores) {
        LinkedHashMap<String, ArrayList<Object>> hashMap = new LinkedHashMap<>();
        Iterator<String> iterator = scores.iterator();
        String next = iterator.next();
        while (iterator.hasNext()) {
            // 处理必修课以及限选课成绩
            if (next.contains("学年")) {
                String term = iterator.next();
                while (term.equals("春") || term.equals("秋")) {
                    ArrayList<Object> objects1 = new ArrayList<>();
                    String title1 = next.split(" ")[0] + "-";
                    if (term.equals("秋")) {
                        title1 += 1;
                    } else {
                        title1 += 2;
                    }
                    String course = "";
                    if (iterator.hasNext()) course = iterator.next();
                    else break;
                    while (!course.contains("平均学分绩点")) {
                        if (iterator.hasNext()) course = iterator.next();
                        else break;

                        String[] s = course.split(" ");
                        if (s.length == 6) {
                            objects1.add(new Score(s[0], s[2], s[3], s[4]));
                        } else if (s.length == 7){
                            objects1.add(new Score(s[0], s[2], s[3], s[5]));
                        }
                    }
                    String[] s = course.split(" ");
                    objects1.add(new Score("【" + s[0], s[1] + "",  s[2], s[3] + "】"));
                    hashMap.put(title1, objects1);

                    if (iterator.hasNext()) term = iterator.next();
                    else break;
                }
                next = term;
                // 处理通选课成绩
            } else if (next.equals("学期 课程 课程号 学分 正考 补考 绩点")) {
                next = iterator.next();
                while (!next.contains("已获得学分")) {
                    String[] s = next.split(" ");
                    ArrayList<Object> objects = hashMap.get(s[0]);
                    if (objects == null) {
                        objects = new ArrayList<>();
                        hashMap.put(s[0], objects);
                    }
                    if (s.length == 6) {
                        ArrayUtils.addSecondLast(objects, new Score(s[1], s[3], "其他", s[4]));
                    } else if (s.length == 7) {
                        ArrayUtils.addSecondLast(objects, new Score(s[1], s[3], "其他", s[5]));
                    }

                    if (iterator.hasNext()) next = iterator.next();
                    else break;
                }

                if (iterator.hasNext()) next = iterator.next();
                else break;

                // 处理CET成绩
            } else if (next.equals("准考证号 考试场次 语言级别 总分 听力 阅读 写作 综合")) {
                next = iterator.next();
                ArrayList<Object> objects;
                boolean flag = false;
                if (hashMap.containsKey("外语等级考试")) {
                    objects = hashMap.get("外语等级考试");
                } else {
                    objects = new ArrayList<>();
                    flag = true;
                }
                while (true) {
                    String[] s = next.split(" ");
                    objects.add(new Score(s[4], "0", "其他", s[5]));

                    if (iterator.hasNext()) next = iterator.next();
                    else break;
                }
                if (flag) {
                    hashMap.put("外语等级考试", objects);
                }
            }
        }
        return hashMap;
    }
}
