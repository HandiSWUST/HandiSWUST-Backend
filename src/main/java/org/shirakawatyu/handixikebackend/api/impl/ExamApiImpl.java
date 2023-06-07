package org.shirakawatyu.handixikebackend.api.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.shirakawatyu.handixikebackend.api.ExamApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.pojo.Exam;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("examApi")
public class ExamApiImpl implements ExamApi {
    private String setExamList(String [] strings){

        ArrayList<Exam> exams = new ArrayList<>();
        HashMap<String, List<Exam>> map = new HashMap<>();

        int examNum = (strings.length-8)/9;
        for(int p =0;p<examNum;p++){
            int aid = 8+p*9;
            if(strings[aid].equals("")){
                ArrayList<Exam> re = new ArrayList<>();
                int start = aid + 1;
                int reNum = (strings.length - 8 - start)/9;
                for (int p2 = 0;p2<reNum;p2++){
                    int aid2 = 8+p2*9;
                    Exam exam = new Exam(strings[start+aid2],strings[start+1+aid2],strings[start+2+aid2],strings[start+3+aid2],strings[start+4+aid2],
                            strings[start+5+aid2],strings[start+6+aid2],strings[start+7+aid2],strings[start+8+aid2]);
                    re.add(exam);
                }
                map.put("补考(已考完的科目仍然显示的话，是教务系统的锅)",re);
                break;
            }
            Exam exam = new Exam(strings[aid],strings[1+aid],strings[2+aid],strings[3+aid],strings[4+aid],
                    strings[5+aid],strings[6+aid],strings[7+aid],strings[8+aid]);
            exams.add(exam);
        }
        map.put("期末考试",exams);
        return JSONObject.toJSONString(map);
    }


    @Override
    public String getExam(RestTemplate restTemplate) {
        Logger log = Logger.getLogger("ExamApiImpl.getExam :  ");
        try {
            Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
            ResponseEntity<String> doc = Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:examTable", "", restTemplate);
            String info = Jsoup.parse(Objects.requireNonNull(doc.getBody())).getElementsByTag("td").text();
            String[] s = info.split(" ");
            if(s[0].equals("")) {
                log.log(Level.WARNING,"教务系统寄了");
                return "sys err";
            }
            if (s.length<9) return "no data";
            return setExamList(s);
        } catch (Exception e){
            e.printStackTrace();
            log.log(Level.SEVERE,"可能是登录凭证过期了，八成不会出这个问题");
        }
        return "s";
    }
}