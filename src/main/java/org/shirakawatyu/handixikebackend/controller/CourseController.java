package org.shirakawatyu.handixikebackend.controller;

import org.shirakawatyu.handixikebackend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CourseController {

    @Autowired
    CourseService courseService;

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

}
