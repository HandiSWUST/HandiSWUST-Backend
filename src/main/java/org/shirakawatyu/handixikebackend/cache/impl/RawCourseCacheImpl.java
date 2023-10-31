package org.shirakawatyu.handixikebackend.cache.impl;

import com.alibaba.fastjson2.JSONException;
import jakarta.annotation.Resource;
import org.shirakawatyu.handixikebackend.api.CourseApi;
import org.shirakawatyu.handixikebackend.cache.RawCourseCache;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.common.ResultCode;
import org.shirakawatyu.handixikebackend.exception.NotLoginException;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ShirakawaTyu
 */
@Service
public class RawCourseCacheImpl implements RawCourseCache {
    @Resource(name="NormalCourseApi")
    private CourseApi normalCourseApi;
    @Resource(name="ExperimentCourseApi")
    private CourseApi experimentCourseApi;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Cacheable(value = "Course", key = "'r'+#p1", unless = "null == #result")
    @Override
    public List<Lesson> getRawCourse(RestTemplate restTemplate, String no) {
        List<Lesson> lessonsArray = new ArrayList<>();
        List<Lesson> normalCourse;
        List<Lesson> experimentCourse;
        try {
            normalCourse = normalCourseApi.getCourse(restTemplate);
            experimentCourse = experimentCourseApi.getCourse(restTemplate);
        } catch (Exception e) {
            throw new NotLoginException();
        }
        lessonsArray.addAll(normalCourse);
        lessonsArray.addAll(experimentCourse);
        if (!lessonsArray.isEmpty()) {
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

    @Override
    public boolean manualDeleteCache(String no) {
        try {
            Set<String> keys = redisTemplate.keys("*" + no + "*");
            if (keys != null) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
