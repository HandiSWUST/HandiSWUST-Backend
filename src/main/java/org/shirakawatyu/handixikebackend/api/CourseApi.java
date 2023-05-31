package org.shirakawatyu.handixikebackend.api;

import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface CourseApi {
    List<Lesson> getCourse(RestTemplate restTemplate);
}
