package org.shirakawatyu.handixikebackend.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.shirakawatyu.handixikebackend.pojo.Lesson;

public class LessonUtils {
    public static Lesson merge(JSONObject lesson1, JSONObject lesson2) {
        String jw_course_code = lesson1.getString("jw_course_code") + " | " + lesson2.getString("jw_course_code");
        String base_teacher_name = null;
        if(!lesson1.getString("base_teacher_name").equals(lesson2.getString("base_teacher_name"))) {
            base_teacher_name = lesson1.getString("base_teacher_name") + " | " + lesson2.getString("base_teacher_name");
        }else {
            base_teacher_name = lesson1.getString("base_teacher_name");
        }
        String base_room_name = null;
        if(!lesson1.getString("base_room_name").equals(lesson2.getString("base_room_name"))) {
            base_room_name = lesson1.getString("base_room_name") + " | " + lesson2.getString("base_room_name");
        }else {
            base_room_name = lesson1.getString("base_room_name");
        }
        String week = lesson1.getString("week") + " | " + lesson2.getString("week");
        String jw_task_book_no = lesson1.getString("jw_task_book_no") + " | " + lesson2.getString("jw_task_book_no");

        String jw_course_name = lesson1.getString("jw_course_name") + " | " + lesson2.getString("jw_course_name");
        jw_course_name = jw_course_name.replace("(重课)", "");
        jw_course_name = "(重课)" + jw_course_name;
        String section_end = lesson1.getString("section_end");
        String week_day = lesson1.getString("week_day");
        String section = lesson1.getString("section");
        String base_teacher_no = lesson1.getString("base_teacher_no") + " | " + lesson2.getString("base_teacher_no");
        String section_start = lesson1.getString("section_start");
        return new Lesson(jw_course_code, base_teacher_name, base_room_name, week, jw_task_book_no, jw_course_name, section_end, week_day, section, base_teacher_no, section_start);
    }

    public static void split(JSONObject lesson, JSONArray array) {
        int start = lesson.getInteger("section_start");
        int end = lesson.getInteger("section_end");
        for (int i = start; i < end; i+=2) {
            array.add(JSON.parseObject(JSON.toJSONString(new Lesson(lesson.getString("jw_course_code"),
                    lesson.getString("base_teacher_name"),
                    lesson.getString("base_room_name"),
                    lesson.getString("week"),
                    lesson.getString("jw_task_book_no"),
                    lesson.getString("jw_course_name"),
                    Integer.toString(i+1),
                    lesson.getString("week_day"),
                    Integer.toString(4),
                    lesson.getString("base_teacher_no"),
                    Integer.toString(i))))) ;
        }
    }

    public static String[] timeProcess(String time) {
        String week = time.split("周")[0];
        String weekday = null;
        switch (time.split("星期")[1].charAt(0)) {
            case '一':
                weekday = "1";
                break;
            case '二':
                weekday = "2";
                break;
            case '三':
                weekday = "3";
                break;
            case '四':
                weekday = "4";
                break;
            case '五':
                weekday = "5";
                break;
            case '六':
                weekday = "6";
                break;
            case '日':
                weekday = "7";
                break;
            default:
                break;
        }
        String sectionStart = time.substring(time.indexOf("星期") + 3, time.indexOf("-"));
        String sectionEnd = time.substring(time.indexOf("-") + 1, time.indexOf("节"));
        String section = Integer.toString((Integer.parseInt(sectionEnd) - Integer.parseInt(sectionStart) + 1) * 2);
        return new String[]{week, weekday, sectionStart, sectionEnd, section};
    }

    public static boolean isCurWeek(String week, int curWeek) {
//        int curWeek = Integer.parseInt(DateUtil.curWeek());
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

    public static void onlySelectWeek(int selectedWeek, JSONArray lessonsArray) {
        int size = lessonsArray.size();
        for (int i = 0; i < size; ) {
            boolean ifPlus = true;
            String week = lessonsArray.getJSONObject(i).getString("week");
            if(week.contains(",")) {
                String[] split = week.split(",");
                boolean flag = false;
                for (int j = 0; j < split.length; j++) {
                    flag = LessonUtils.isCurWeek(split[j], selectedWeek);
                }
                if(!flag) {
                    lessonsArray.remove(i);
                    size--;
                    ifPlus = false;
                }
            }else {
                if(!LessonUtils.isCurWeek(week, selectedWeek)) {
                    lessonsArray.remove(i);
                    size--;
                    ifPlus = false;
                }
            }
            if(ifPlus) {
                i++;
            }
        }
    }

    public static void process(JSONArray lessonsArray) {
        // 节数大于2处理
        int size = lessonsArray.size();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = lessonsArray.getJSONObject(i);
            if(Integer.parseInt(jsonObject.getString("section_end")) - Integer.parseInt(jsonObject.getString("section_start")) > 1) {
                lessonsArray.remove(i);
                LessonUtils.split(jsonObject, lessonsArray);
                size++;
            }
        }
        // 重课处理
        for (int i = 0; i < size; ) {
            JSONObject object = lessonsArray.getJSONObject(i);
            boolean flag = true;
            for (int j = 0; j < size; j++) {
                JSONObject o = lessonsArray.getJSONObject(j);
                if (j != i && object.get("section_start").equals(o.get("section_start")) && object.get("week_day").equals(o.get("week_day"))) {
                    Lesson merge = LessonUtils.merge(object, o);
                    lessonsArray.remove(i);
                    if (i < j) {
                        lessonsArray.remove(j - 1);
                    } else {
                        lessonsArray.remove(j);
                    }
                    String s = JSON.toJSONString(merge);
                    JSONObject object1 = JSON.parseObject(s);
                    lessonsArray.add(object1);
                    size--;
                    flag = false;
                    break;
                }
            }
            if (flag) {
                i++;
            }
        }
    }
}
