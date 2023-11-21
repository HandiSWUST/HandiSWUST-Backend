package org.shirakawatyu.handixikebackend.api.impl;

import org.jsoup.Jsoup;
import org.shirakawatyu.handixikebackend.api.ScoreApi;
import org.shirakawatyu.handixikebackend.pojo.GradePointAverage;
import org.shirakawatyu.handixikebackend.pojo.Score;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.shirakawatyu.handixikebackend.utils.ScoreUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ShirakawaTyu
 */
@Component("MatrixScoreApi")
public class MatrixScoreApi implements ScoreApi {

    private static final String scoreUrl = "https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm";

    @Override
    public GradePointAverage getGradePointAverage(RestTemplate restTemplate) {
        String allGPA = "", requiredGPA = "", resp = "";
        try {
            Requests.post(
                    "http://cas.swust.edu.cn/authserver/login?service=" + scoreUrl + "?event=studentPortal:DEFAULT_EVENT",
                    new LinkedMultiValueMap<>(), restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get(scoreUrl + "?event=studentProfile:courseMark", "", restTemplate);
            resp = responseEntity1.getBody();
            List<String> list = Jsoup.parse(Objects.requireNonNull(resp)).getElementsByClass("boxNavigation").eachText();
            if (list.isEmpty()) {
                return null;
            }
            // 教务系统当前还没有你的成绩
            if ("0".equals(list.get(0).split(" ")[0])) {
                return new GradePointAverage(0, 0);
            }
            String[] s = list.get(1).split(" ");
            allGPA = s[0].replace("平均绩点", "");
            requiredGPA = s[1].replace("必修课绩点", "");
            return new GradePointAverage(Double.parseDouble(allGPA), Double.parseDouble(requiredGPA));
        } catch (Exception e) {
            Logger.getLogger("MatrixScoreApi.getGradePointAverage => ").log(Level.SEVERE, resp);
            throw e;
        }
    }

    @Override
    public LinkedHashMap<String, ArrayList<Score>> getScore(RestTemplate restTemplate) {
        List<String> scores = null;
        String resp = "";
        try {
            Requests.post(
                    "http://cas.swust.edu.cn/authserver/login?service=" + scoreUrl + "?event=studentPortal:DEFAULT_EVENT",
                    new LinkedMultiValueMap<>(), restTemplate);
            ResponseEntity<String> responseEntity1 = Requests.get(scoreUrl + "?event=studentProfile:courseMark", "", restTemplate);
            resp = responseEntity1.getBody();
            scores = Jsoup.parse(resp).getElementsByClass("UItable").select("tr").eachText();
            if (scores.isEmpty()) {
                return null;
            }
            return processScore(scores);
        } catch (Exception e) {
            if (scores != null) {
                Logger.getLogger("MatrixScoreApi.getScore => ").log(Level.SEVERE, resp);
            }
            throw e;
        }
    }

    private LinkedHashMap<String, ArrayList<Score>> processScore(List<String> scoreStings) {
        Queue<String> scores = new LinkedList<>(scoreStings);
        LinkedHashMap<String, ArrayList<Score>> hashMap = new LinkedHashMap<>();
        ScoreUtils.requiredScoreFilter(scores, hashMap);
        ScoreUtils.optionalScoreFilter(scores, hashMap);
        ScoreUtils.cetScoreFilter(scores, hashMap);
        if (hashMap.isEmpty()) {
            return null;
        }
        if (!scores.isEmpty()) {
            Logger.getLogger("MatrixScoreApi.processScore => ").log(Level.WARNING, scores.toString());
        }
        return hashMap;
    }
}
