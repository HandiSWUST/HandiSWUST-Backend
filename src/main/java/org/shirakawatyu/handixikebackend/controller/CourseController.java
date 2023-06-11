package org.shirakawatyu.handixikebackend.controller;

import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourseController {

    @Autowired
    CourseService courseService;

    // no为学号，用于存取缓存的Key

    @GetMapping("/api/v2/course/all")
    public Result course(HttpSession session) {
        String no = (String) session.getAttribute("no");
        return courseService.course(session, no);
    }

    @GetMapping("/api/v2/course/cur")
    public Result curCourse(HttpSession session) {
        String no = (String) session.getAttribute("no");
        return courseService.courseCurWeek(session, no);
    }
    @GetMapping("/api/v2/course/select/{selectedWeek}")
    public Result selectedCourse(@PathVariable int selectedWeek, HttpSession session) {
        String no = (String) session.getAttribute("no");
        return courseService.courseSelectedWeek(session, no, selectedWeek);

    }

    @PostMapping("/api/v2/course/local/{selectedWeek}")
    public Result useLocalCourse(@PathVariable int selectedWeek, @RequestBody String courseData) {
        return courseService.useLocalCourse(selectedWeek, courseData);
    }

}
