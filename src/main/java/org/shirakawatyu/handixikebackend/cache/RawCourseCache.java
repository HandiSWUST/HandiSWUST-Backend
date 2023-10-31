package org.shirakawatyu.handixikebackend.cache;

import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface RawCourseCache {
    List<Lesson> getRawCourse(RestTemplate restTemplate, String no);
    void deleteCache();
}
