package org.shirakawatyu.handixikebackend.api;

import org.springframework.web.client.RestTemplate;

public interface ExamApi {
    public String getExam(RestTemplate restTemplate);
}
