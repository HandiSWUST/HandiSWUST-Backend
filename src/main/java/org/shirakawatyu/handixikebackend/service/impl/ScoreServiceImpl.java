package org.shirakawatyu.handixikebackend.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.pojo.Score;
import org.shirakawatyu.handixikebackend.service.ScoreService;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ScoreServiceImpl implements ScoreService {

//    RestTemplate restTemplate;





//    public String setTerm(String term){
//
//        String year = term.substring(0,2);
//        int i = Integer.parseInt(year) + 1;
//        String nextYear = Integer.toString(i);
//        String th = term.substring(2);
//        return year+"-"+nextYear+" "+"第"+th+"学期";
//    }
    @Override
    public String getScore(HttpSession session) {
        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");
        try {
            Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentProfile:courseMark", "", restTemplate);
            List<String> scores = Jsoup.parse(responseEntity1.getBody()).getElementsByClass("UItable").select("tr").eachText();
            return JSON.toJSONString(processScore(scores));
        }catch (Exception e) {
            return "3401 LOGOUT";
        }
//        try{
//
//            ResponseEntity<String> entity0 = Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage#view_index", "", restTemplate);
//            ResponseEntity<String> entity = Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/studentMark", "http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage#view_index", restTemplate);
//            Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/studentInfo","http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage#view_index", restTemplate);
//            JSONObject jsonObject = JSONObject.parseObject(entity.getBody());
//            HashMap<String, ArrayList<Object>>map = new HashMap<>();
//            JSONObject body = (JSONObject)jsonObject.get("body");
//
//            if(body == null) {
//                return null;
//            }
//            String result = body.get("result").toString();
//            JSONArray jsonArray = JSONObject.parseArray(result);
//            for(Object s : jsonArray.toArray()){
//                JSONObject json = (JSONObject) s;
//                if(map.get(setTerm((String)json.get("term")))==null){
//                    ArrayList<Object> objects = new ArrayList<>();
//                    objects.add(s);
//                    map.put(setTerm((String)json.get("term")),objects);}
//                else {
//                    ArrayList<Object> term = map.get(setTerm((String)json.get("term")));
//                    term.add(s);
//                    map.put(setTerm((String)json.get("term")), term);
//                }
//            }
//
//            return JSONObject.toJSONString(map);
//        }
//        catch (NullPointerException e){
//            return null;
//        }

    }
    @Override
    public String getGPA(HttpSession session) {
        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");
        try {
            Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentProfile:courseMark", "", restTemplate);
            List<String> list = Jsoup.parse(responseEntity1.getBody()).getElementsByClass("boxNavigation").eachText();
            String[] s = list.get(1).split(" ");
            JSONObject gpa = new JSONObject();
            gpa.put("all", s[0].replace("平均绩点", ""));
            gpa.put("required", s[1].replace("必修课绩点", ""));
            JSONObject result = new JSONObject();
            result.put("gpa", gpa);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            return "3401 LOGOUT";
        }

//        try {
//            ResponseEntity<String> entity0 = Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage#view_index", "", restTemplate);
//
//            ResponseEntity<String> entity = Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/studentInfo", "http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage#view_index", restTemplate);
//            return JSONObject.toJSONString(entity.getBody());
//        }catch (NullPointerException e){
//            return null;
//        }
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
                    String course = iterator.next();
                    while (!course.contains("平均学分绩点")) {
                        course = iterator.next();
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
                    term = iterator.next();
                }
                next = term;
                // 处理通选课成绩
            } else if (next.equals("学期 课程 课程号 学分 正考 补考 绩点")) {
                next = iterator.next();
                while (!next.contains("已获得学分")) {
                    String[] s = next.split(" ");
                    ArrayList<Object> objects = hashMap.get(s[0]);
                    if (s.length == 6) {
                        ArrayUtils.addSecondLast(objects, new Score(s[1], s[3], "其他", s[5]));
                    } else if (s.length == 7) {
                        ArrayUtils.addSecondLast(objects, new Score(s[1], s[3], "其他", s[5]));
                    }
                    next = iterator.next();
                }
                next = iterator.next();
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
                    if (iterator.hasNext()) {
                        next = iterator.next();
                    } else {
                        break;
                    }
                }
                if (flag) {
                    hashMap.put("外语等级考试", objects);
                }
            }
        }
        return hashMap;
    }

}