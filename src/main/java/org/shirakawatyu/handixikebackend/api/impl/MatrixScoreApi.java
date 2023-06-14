package org.shirakawatyu.handixikebackend.api.impl;

import org.jsoup.Jsoup;
import org.shirakawatyu.handixikebackend.api.ScoreApi;
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
        String allGPA = "", requiredGPA = "";
        try {
            Requests.get( scoreUrl + "?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get(scoreUrl + "?event=studentProfile:courseMark", "", restTemplate);
            List<String> list = Jsoup.parse(Objects.requireNonNull(responseEntity1.getBody())).getElementsByClass("boxNavigation").eachText();
            if (list.size() == 0)
                return null;
            String[] s = list.get(1).split(" ");
            allGPA = s[0].replace("平均绩点", "");
            requiredGPA = s[1].replace("必修课绩点", "");
            return new GradePointAverage(Double.parseDouble(allGPA), Double.parseDouble(requiredGPA));
        } catch (Exception e) {
            Logger.getLogger("MatrixScoreApi.getGradePointAverage => ").log(Level.SEVERE, "allGPA: " + allGPA + "requiredGPA: " + requiredGPA);
            throw e;
        }
    }

    @Override
    public LinkedHashMap<String, ArrayList<Object>> getScore(RestTemplate restTemplate) {
        List<String> scores = null;
        try {
            Requests.get( scoreUrl + "?event=studentPortal:DEFAULT_EVENT", "", restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get(scoreUrl + "?event=studentProfile:courseMark", "", restTemplate);
            scores = Jsoup.parse(responseEntity1.getBody()).getElementsByClass("UItable").select("tr").eachText();
            if (scores.size() == 0)
                return null;
            return processScore(scores);
        } catch (Exception e) {
            if (scores != null)
                Logger.getLogger("MatrixScoreApi.getScore => ").log(Level.SEVERE, scores.toString());
            throw e;
        }
    }

    private LinkedHashMap<String, ArrayList<Object>> processScore(List<String> scoreStings) {
        Queue<String> scores = new LinkedList<>(scoreStings);
        LinkedHashMap<String, ArrayList<Object>> hashMap = new LinkedHashMap<>();
        ScoreUtils.requiredScoreFilter(scores, hashMap);
        ScoreUtils.optionalScoreFilter(scores, hashMap);
        ScoreUtils.cetScoreFilter(scores, hashMap);
        if (hashMap.size() == 0)
            return null;
        if (!scores.isEmpty())
            Logger.getLogger("MatrixScoreApi.processScore => ").log(Level.WARNING, scores.toString());
        return hashMap;
    }
}
