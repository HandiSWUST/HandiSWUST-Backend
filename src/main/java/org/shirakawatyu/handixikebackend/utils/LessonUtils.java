package org.shirakawatyu.handixikebackend.utils;

import com.alibaba.fastjson2.JSON;
import org.shirakawatyu.handixikebackend.pojo.Lesson;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LessonUtils {
    /**
     * 将两节课合并为一节
     * @param lesson1 课程1
     * @param lesson2 课程2
     * @return Lesson
     * @author ShirakawaTyu
     */
    public static Lesson merge(Lesson lesson1, Lesson lesson2) {
        String jw_course_code = lesson1.getJw_course_code() + " | " + lesson2.getJw_course_code();
        String base_teacher_name;
        if (!lesson1.getBase_teacher_name().equals(lesson2.getBase_teacher_name())) {
            base_teacher_name = lesson1.getBase_teacher_name() + " | " + lesson2.getBase_teacher_name();
        } else {
            base_teacher_name = lesson1.getBase_teacher_name();
        }
        String base_room_name;
        if (!lesson1.getBase_room_name().equals(lesson2.getBase_room_name())) {
            base_room_name = lesson1.getBase_room_name() + " | " + lesson2.getBase_room_name();
        } else {
            base_room_name = lesson1.getBase_room_name();
        }
        String week = lesson1.getWeek() + " | " + lesson2.getWeek();
        String jw_task_book_no = lesson1.getJw_task_book_no() + " | " + lesson2.getJw_task_book_no();
        String jw_course_name = lesson1.getJw_course_name() + " | " + lesson2.getJw_course_name();
        jw_course_name = jw_course_name.replace("(重课)", "");
        jw_course_name = "(重课)" + jw_course_name;
        String section_end = lesson1.getSection_end();
        String week_day = lesson1.getWeek_day();
        String section = lesson1.getSection();
        String base_teacher_no = lesson1.getBase_teacher_no() + " | " + lesson2.getBase_teacher_no();
        String section_start = lesson1.getSection_start();
        return new Lesson(jw_course_code, base_teacher_name, base_room_name, week, jw_task_book_no, jw_course_name, section_end, week_day, section, base_teacher_no, section_start);
    }

    /**
     * 将节数大于2的课拆成多节
     * @param lesson 课程
     * @param iterator 课程列表迭代器
     * @author ShirakawaTyu
     */
    public static void split(Lesson lesson, ListIterator<Lesson> iterator) {
        int start = Integer.parseInt(lesson.getSection_start());
        int end = Integer.parseInt(lesson.getSection_end());
        for (int i = start; i < end; i+=2) {
            iterator.add(new Lesson(lesson.getJw_course_code(),
                    lesson.getBase_teacher_name(),
                    lesson.getBase_room_name(),
                    lesson.getWeek(),
                    lesson.getJw_task_book_no(),
                    lesson.getJw_course_name(),
                    Integer.toString(i + 1),
                    lesson.getWeek_day(),
                    Integer.toString(4),
                    lesson.getBase_teacher_no(),
                    Integer.toString(i)));
        }
    }

    /** 
     * 将形如 "9周星期二11-12节" 的时间字符串转换为 "[9, 2, 11, 12, 2]"
     * @param time 时间字符串，形如 "9周星期二11-12节"
     * @return String[] 格式为 "[周, 星期, 开始节数, 结束节数, 总课程长度]"
     * @author ShirakawaTyu
     */
    public static String[] timeProcess(String time) {
        String week = time.split("周")[0];
        String weekday = null;
        try {
            switch (time.split("星期")[1].charAt(0)) {
                case '一' -> weekday = "1";
                case '二' -> weekday = "2";
                case '三' -> weekday = "3";
                case '四' -> weekday = "4";
                case '五' -> weekday = "5";
                case '六' -> weekday = "6";
                case '日' -> weekday = "7";
                default -> {
                }
            }
        }catch (Exception e) {
            Logger.getLogger("timeProcess,weekday =>").log(Level.WARNING, time);
            return null;
        }
        String sectionStart = time.substring(time.indexOf("星期") + 3, time.indexOf("-"));
        String sectionEnd = time.substring(time.indexOf("-") + 1, time.indexOf("节"));
        String section = Integer.toString((Integer.parseInt(sectionEnd) - Integer.parseInt(sectionStart) + 1) * 2);
        return new String[]{week, weekday, sectionStart, sectionEnd, section};
    }

    /**
     * 判断指定周数是否在所给的范围内
     * @param week 周数范围字符串，形如 1-12
     * @param curWeek 指定周数
     * @return boolean
     * @author ShirakawaTyu
     */
    public static boolean isCurWeek(String week, int curWeek) {
        if(week.contains("-")) {
            String[] strings = week.split("-");
            if(strings.length == 2) {
                return curWeek >= Integer.parseInt(strings[0]) && curWeek <= Integer.parseInt(strings[1]);
            }
        }else {
            return curWeek == Integer.parseInt(week);
        }
        return false;
    }

    /**
     * 把不属于所选周的课程删掉，只留下所选周的
     * @param selectedWeek 选择的周数
     * @param lessonsArray 要筛选的课程列表
     * @author ShirakawaTyu
     */
    public static void onlySelectWeek(int selectedWeek, List<Lesson> lessonsArray) {
        ListIterator<Lesson> iterator = lessonsArray.listIterator();
        while (iterator.hasNext()) {
            String week = iterator.next().getWeek();
            if (week.contains(",")) {
                String[] split = week.split(",");
                boolean inRange = false;
                for (String s : split) {
                    inRange = LessonUtils.isCurWeek(s, selectedWeek);
                    if (inRange) {
                        break;
                    }
                }
                if (!inRange) {
                    iterator.remove();
                }
            } else {
                if (!LessonUtils.isCurWeek(week, selectedWeek)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 对节数大于2的课程切割，对冲突的课程合并
     * @param lessonsArray 要处理的课程列表
     * @author ShirakawaTyu
     */
    public static void process(List<Lesson> lessonsArray) {
        ListIterator<Lesson> iterator = lessonsArray.listIterator();
        // 节数大于2处理，将其切割成两节
        while(iterator.hasNext()) {
            Lesson lesson = iterator.next();
            if(Integer.parseInt(lesson.getSection_end()) - Integer.parseInt(lesson.getSection_start()) > 1) {
                iterator.remove();
                LessonUtils.split(lesson, iterator);
            }
        }

        // 重课处理，两节合并为一节
        for (int i = 0; i < lessonsArray.size(); i++) {
            Lesson lesson1 = lessonsArray.get(i);
            for (int j = 0; lesson1 != null && j < lessonsArray.size(); j++) {
                Lesson lesson2 = lessonsArray.get(j);
                if (lesson2 != null && j != i && lesson1.getSection_start().equals(lesson2.getSection_start()) && lesson1.getWeek_day().equals(lesson2.getWeek_day())) {
                    Lesson merge = LessonUtils.merge(lesson1, lesson2);
                    lessonsArray.set(i, null);
                    lessonsArray.set(j, null);
                    lessonsArray.add(merge);
                    break;
                }
            }
        }
        iterator = lessonsArray.listIterator();
        while (iterator.hasNext()) {
            if(iterator.next() == null) {
                iterator.remove();
            }
        }
    }

    /**
     * 简单的获得所选周课程的方法
     * @param selectedWeek 选择的周数
     * @param lessonsArray 课程列表
     * @return String
     * @author ShirakawaTyu
     */
    public static String simpleSelectWeek(int selectedWeek, List<Lesson> lessonsArray) {
        LessonUtils.onlySelectWeek(selectedWeek, lessonsArray);
        LessonUtils.process(lessonsArray);
        if(lessonsArray.size() > 0) {
            return JSON.toJSONString(lessonsArray);
        }
        return "[]";
    }
}
