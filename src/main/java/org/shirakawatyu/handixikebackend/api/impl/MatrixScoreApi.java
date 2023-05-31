package org.shirakawatyu.handixikebackend.api.impl;

import com.alibaba.fastjson2.JSON;
import org.jsoup.Jsoup;
import org.shirakawatyu.handixikebackend.api.ScoreApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.pojo.GradePointAverage;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.shirakawatyu.handixikebackend.utils.ScoreUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("MatrixScoreApi")
public class MatrixScoreApi implements ScoreApi {

    private static final String scoreUrl = "https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm";

    @Override
    public GradePointAverage getGradePointAverage(RestTemplate restTemplate) {
        try {
            Requests.get( scoreUrl + "?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get(scoreUrl + "?event=studentProfile:courseMark", "", restTemplate);
            List<String> list = Jsoup.parse(Objects.requireNonNull(responseEntity1.getBody())).getElementsByClass("boxNavigation").eachText();
            String[] s = list.get(1).split(" ");
            String allGPA = s[0].replace("平均绩点", "");
            String requiredGPA = s[1].replace("必修课绩点", "");
            return new GradePointAverage(Double.parseDouble(allGPA), Double.parseDouble(requiredGPA));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LinkedHashMap<String, ArrayList<Object>> getScore(RestTemplate restTemplate) {
        List<String> scores = null;
        try {
            Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentProfile:courseMark", "", restTemplate);
            scores = Jsoup.parse(responseEntity1.getBody()).getElementsByClass("UItable").select("tr").eachText();
            return processScore(scores);
        } catch (Exception e) {
            e.printStackTrace();
            if (scores != null)
                Logger.getLogger("MatrixScoreApi => ").log(Level.WARNING, scores.toString());
        }
        return null;
    }

    private LinkedHashMap<String, ArrayList<Object>> processScore(List<String> scoreStings) {
        Queue<String> scores = new LinkedList<>(scoreStings);
        LinkedHashMap<String, ArrayList<Object>> hashMap = new LinkedHashMap<>();
        ScoreUtils.requiredScoreFilter(scores, hashMap);
        ScoreUtils.optionalScoreFilter(scores, hashMap);
        ScoreUtils.cetScoreFilter(scores, hashMap);
        if (hashMap.size() == 0) return null;
        return hashMap;
    }
}
