package org.shirakawatyu.handixikebackend.api;

import org.shirakawatyu.handixikebackend.pojo.GradePointAverage;
import org.shirakawatyu.handixikebackend.pojo.Score;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface ScoreApi {
    GradePointAverage getGradePointAverage(RestTemplate restTemplate);
    LinkedHashMap<String, ArrayList<Score>> getScore(RestTemplate restTemplate);
}
