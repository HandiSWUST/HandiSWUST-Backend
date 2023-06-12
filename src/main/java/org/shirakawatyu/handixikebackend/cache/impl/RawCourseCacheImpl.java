package org.shirakawatyu.handixikebackend.cache.impl;

import jakarta.annotation.Resource;
import org.shirakawatyu.handixikebackend.api.CourseApi;
import org.shirakawatyu.handixikebackend.cache.RawCourseCache;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RawCourseCacheImpl implements RawCourseCache {
    @Resource(name="NormalCourseApi")
    private CourseApi normalCourseApi;
    @Resource(name="ExperimentCourseApi")
    private CourseApi experimentCourseApi;

    @Cacheable(value = "Course", key = "'r'+#p1", unless = "null == #result")
    @Override
    public List<Lesson> getRawCourse(RestTemplate restTemplate, String no) {
        List<Lesson> lessonsArray = new ArrayList<>();
        List<Lesson> normalCourse = normalCourseApi.getCourse(restTemplate);
        List<Lesson> experimentCourse = experimentCourseApi.getCourse(restTemplate);
        if (normalCourse == null || experimentCourse == null) {
            return null;
        }
        lessonsArray.addAll(normalCourse);
        lessonsArray.addAll(experimentCourse);
        if (lessonsArray.size() > 0) {
            ArrayUtils.nullObjChk(lessonsArray);
            return lessonsArray;
        }
        return null;
    }

    @Scheduled(cron = "0 0 1 * * ? ")
    @CacheEvict(value = "Course", allEntries = true)
    @Override
    public void deleteCache() {
        Logger.getLogger("RawCourseCacheImpl => ").log(Level.INFO, "已清理缓存");
    }
}
