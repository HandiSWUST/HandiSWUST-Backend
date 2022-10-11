package org.shirakawatyu.handixikebackend.service;

import com.alibaba.fastjson2.JSONArray;
import org.springframework.web.client.RestTemplate;

public interface CacheRawCourseService {
    JSONArray getRawCourse(RestTemplate restTemplate, long no);
}
