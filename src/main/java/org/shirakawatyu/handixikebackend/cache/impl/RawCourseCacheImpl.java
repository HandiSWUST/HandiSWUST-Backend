package org.shirakawatyu.handixikebackend.cache.impl;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.shirakawatyu.handixikebackend.api.CourseApi;
import org.shirakawatyu.handixikebackend.cache.RawCourseCache;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ShirakawaTyu
 */
@Service
@RequiredArgsConstructor
public class RawCourseCacheImpl implements RawCourseCache {

    private final List<CourseApi> courseApis;
    private final StringRedisTemplate redisTemplate;

    @Cacheable(value = "Course", key = "'r'+#p1", unless = "null == #result")
    @Override
    public List<Lesson> getRawCourse(CookieStore cookieStore, String no) {
        HashSet<Lesson> lessonsResultSet = new HashSet<>();
        for (CourseApi api : courseApis) {
            lessonsResultSet.addAll(api.getCourse(cookieStore));
        }
        if (!lessonsResultSet.isEmpty()) {
            ArrayList<Lesson> lessonList = new ArrayList<>(lessonsResultSet);
            ArrayUtils.nullObjChk(lessonList);
            return lessonList;
        }
        return null;
    }

    @Scheduled(cron = "0 0 1 * * ? ")
    @CacheEvict(value = "Course", allEntries = true)
    @Override
    public void deleteCache() {
//        TODO 异步unlink 减少slow log (有用但不多
//        ScanOptions options = KeyScanOptions.scanOptions()
//                .match("Course::*")
//                .count(1000)
//                .build();
//        new ArrayList<>();
//
//        try (Cursor<String> cursor = redisTemplate.scan(options)) {
//            while (cursor.hasNext()) {
//                redisTemplate.unlink(cursor.next());
//            }
//        }

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
