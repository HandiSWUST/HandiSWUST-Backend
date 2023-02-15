package org.shirakawatyu.handixikebackend.controller;

import org.shirakawatyu.handixikebackend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CourseController {

    @Autowired
    CourseService courseService;

    // no为学号，用于存取缓存的Key

    @GetMapping("/api/course/all")
    @ResponseBody
    public String course(HttpSession session) {
        long no = Long.parseLong((String) session.getAttribute("no"));
        return courseService.course(session, no);
    }

    @GetMapping("/api/course/cur")
    @ResponseBody
    public String curCourse(HttpSession session) {
        long no = Long.parseLong((String) session.getAttribute("no"));
        return courseService.courseCurWeek(session, no);
    }
    @GetMapping("/api/course/select/{selectedWeek}")
    @ResponseBody
    public String selectedCourse(@PathVariable int selectedWeek, HttpSession session) {
        long no = Long.parseLong((String) session.getAttribute("no"));
        return courseService.courseSelectedWeek(session, no, selectedWeek);

    }

    @PostMapping("/api/course/local/{selectedWeek}")
    @ResponseBody
    public String useLocalCourse(@PathVariable int selectedWeek, @RequestBody String courseData) {
        return courseService.useLocalCourse(selectedWeek, courseData);
    }

    @PostMapping("/api/course/push/save/{qq}")
    @ResponseBody
    public String savePushData(@PathVariable long qq, @RequestBody String courseData, HttpSession session) {
        return courseService.savePushData(qq, courseData, session);
    }

    @GetMapping("/api/course/push/test")
    @ResponseBody
    public String testPush(HttpSession session) {
        return courseService.testPush((String) session.getAttribute("no"));
    }

    @DeleteMapping("/api/course/push/delete")
    @ResponseBody
    public String deletePushData(HttpSession session) {
        return courseService.deletePushData((String) session.getAttribute("no"));
    }

    @GetMapping("/api/course/push/check")
    public String checkPush(HttpSession session) {
        return courseService.checkPush((String) session.getAttribute("no"));
    }
}
