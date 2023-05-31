package org.shirakawatyu.handixikebackend.pojo;

import java.io.Serializable;


public class Lesson implements Serializable {
    String jw_course_code;
    String base_teacher_name;
    String base_room_name;
    String week;
    String jw_task_book_no;
    String jw_course_name;
    String section_end;
    String week_day;
    String section;
    String base_teacher_no;
    String section_start;

    public Lesson() {
    }

    public Lesson(String jw_course_code, String base_teacher_name, String base_room_name, String week, String jw_task_book_no, String jw_course_name, String section_end, String week_day, String section, String base_teacher_no, String section_start) {
        this.jw_course_code = jw_course_code;
        this.base_teacher_name = base_teacher_name;
        this.base_room_name = base_room_name;
        this.week = week;
        this.jw_task_book_no = jw_task_book_no;
        this.jw_course_name = jw_course_name;
        this.section_end = section_end;
        this.week_day = week_day;
        this.section = section;
        this.base_teacher_no = base_teacher_no;
        this.section_start = section_start;
    }

    public String getJw_course_code() {
        return jw_course_code;
    }

    public void setJw_course_code(String jw_course_code) {
        this.jw_course_code = jw_course_code;
    }

    public String getBase_teacher_name() {
        return base_teacher_name;
    }

    public void setBase_teacher_name(String base_teacher_name) {
        this.base_teacher_name = base_teacher_name;
    }

    public String getBase_room_name() {
        return base_room_name;
    }

    public void setBase_room_name(String base_room_name) {
        this.base_room_name = base_room_name;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getJw_task_book_no() {
        return jw_task_book_no;
    }

    public void setJw_task_book_no(String jw_task_book_no) {
        this.jw_task_book_no = jw_task_book_no;
    }

    public String getJw_course_name() {
        return jw_course_name;
    }

    public void setJw_course_name(String jw_course_name) {
        this.jw_course_name = jw_course_name;
    }

    public String getSection_end() {
        return section_end;
    }

    public void setSection_end(String section_end) {
        this.section_end = section_end;
    }

    public String getWeek_day() {
        return week_day;
    }

    public void setWeek_day(String week_day) {
        this.week_day = week_day;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBase_teacher_no() {
        return base_teacher_no;
    }

    public void setBase_teacher_no(String base_teacher_no) {
        this.base_teacher_no = base_teacher_no;
    }

    public String getSection_start() {
        return section_start;
    }

    public void setSection_start(String section_start) {
        this.section_start = section_start;
    }
}
