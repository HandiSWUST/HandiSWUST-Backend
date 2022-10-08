package org.shirakawatyu.handixikebackend.controller;

import org.shirakawatyu.handixikebackend.service.CourseService;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
        if(session.getAttribute("status") == null) {
            return "3401 LOGOUT";
        }
        long no = Long.parseLong((String) session.getAttribute("no"));
        return courseService.course(ArrayUtils.arrayToList((Object[]) session.getAttribute("cookies")), session, no);
    }

    @GetMapping("/api/course/cur")
    @ResponseBody
    public String curCourse(HttpSession session) {
        if(session.getAttribute("status") == null) {
            return "3401 LOGOUT";
        }
        Object cookies = session.getAttribute("cookies");
        List<String> list = null;
        try {
            list = ArrayUtils.arrayToList((Object[]) cookies);
        }catch (Exception e) {
            return "3401 LOGOUT";
        }
        long no = Long.parseLong((String) session.getAttribute("no"));
        return courseService.courseCurWeek(list, session, no);
    }


}
