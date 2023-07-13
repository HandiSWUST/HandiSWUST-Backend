package org.shirakawatyu.handixikebackend.utils;

import org.shirakawatyu.handixikebackend.pojo.Lesson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrayUtils {

    // 固定添加至倒数第二项
    public static <T> void addSecondLast(ArrayList<T> arrayList, T o) {
        int lastIndex = arrayList.size() - 1 ;
        if (lastIndex < 0) {
            arrayList.add(o);
            return;
        }
        T last = arrayList.get(lastIndex);
        arrayList.remove(lastIndex);
        arrayList.add(o);
        arrayList.add(last);
    }

    // 检查是否有名字为null的课程
    public static boolean nullObjChk(List<Lesson> jsonArray) {
        // 这个map用于存放获取为null的课程，以后可以考虑分出去用一个单独的文件存放
        HashMap<String, String> map = new HashMap<>();
        map.put("MY220040", "毛泽东思想和中国特色社会主义理论体系概论");
        map.put("MY220030", "习近平新时代中国特色社会主义思想概论");

        boolean flag = false;
        for (int i = 0; i < jsonArray.size(); i++) {
            Lesson o = jsonArray.get(i);
            if(o.getJw_course_name() == null) {
                o.setJw_course_name(map.get(o.getJw_course_code()));
                jsonArray.set(jsonArray.indexOf(o), o);
                flag = true;
            }
        }
        return flag;
    }
}
