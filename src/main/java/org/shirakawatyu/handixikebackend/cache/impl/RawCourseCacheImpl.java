package org.shirakawatyu.handixikebackend.cache.impl;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.shirakawatyu.handixikebackend.api.CourseApi;
import org.shirakawatyu.handixikebackend.api.impl.NormalCourseApi;
import org.shirakawatyu.handixikebackend.cache.RawCourseCache;
import org.shirakawatyu.handixikebackend.common.Constants;
import org.shirakawatyu.handixikebackend.exception.NotLoginException;
import org.shirakawatyu.handixikebackend.pojo.Lesson;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;

/**
 * @author ShirakawaTyu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RawCourseCacheImpl implements RawCourseCache {

    private final List<CourseApi> courseApis;
    private final StringRedisTemplate redisTemplate;
    @Resource(name = "NormalCourseApi")
    private NormalCourseApi normalCourseApi;
    @Resource(name = "ExperimentCourseApi")
    private CourseApi experimentCourseApi;

    @Cacheable(value = "Course", key = "'r'+#p1", unless = "null == #result")
    @Override
    public List<Lesson> getRawCourse(CookieStore cookieStore, String no) {
        HashSet<Lesson> lessonsResultSet = new HashSet<>();
        List<FutureTask<List<Lesson>>> tasks = courseApis.stream().map(api -> {
            FutureTask<List<Lesson>> task = new FutureTask<>(() -> api.getCourse(cookieStore));
            Thread.startVirtualThread(task);
            return task;
        }).toList();
        tasks.forEach(task -> {
            try {
                lessonsResultSet.addAll(task.get());
            } catch (NotLoginException nle) {
                if (nle.getMessage() != null && nle.getMessage().contains("非法登录")) {
                    log.warn("{} :非法登录", no);
                }
                throw nle;
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        });
        if (!lessonsResultSet.isEmpty()) {
            ArrayList<Lesson> lessonList = new ArrayList<>(lessonsResultSet);
            ArrayUtils.nullObjChk(lessonList);
            return lessonList;
        } else if ("1".equals(Constants.CURRENT_TERM.split("-")[2]) &&
                no.substring(0, 6).contains(String.valueOf(LocalDate.now().getYear()))) {
            // 新生可以用教务系统
            lessonsResultSet.addAll(normalCourseApi.getCourseFromMatrix(cookieStore));
            return new ArrayList<>(lessonsResultSet);
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
        log.info("已清理缓存");
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

    @Cacheable(value = "Course", key = "'e'+#p1", unless = "null == #result")
    @Override
    public List<Lesson> getExperimentCourse(CookieStore cookieStore, String no) {
        List<Lesson> course = experimentCourseApi.getCourse(cookieStore);
        ArrayUtils.nullObjChk(course);
        return course;
    }

    @Cacheable(value = "Course", key = "'n'+#p1", unless = "null == #result")
    @Override
    public List<Lesson> getNormalCourse(CookieStore cookieStore, String no) {
        List<Lesson> course = normalCourseApi.getCourse(cookieStore);
        ArrayUtils.nullObjChk(course);
        return course;
    }
}
