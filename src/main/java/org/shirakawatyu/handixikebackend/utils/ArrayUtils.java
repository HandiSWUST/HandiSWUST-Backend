package org.shirakawatyu.handixikebackend.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrayUtils {
    public static List<String> arrayToList(Object[] arr) {
        List<String> list = new ArrayList<>();
        if(arr != null) {
            for (int i = 0; i < arr.length; i++) {
                list.add((String) arr[i]);
            }
        }
        return list;
    }

    public static boolean nullObjChk(JSONArray jsonArray) {
        // 这个map用于存放获取为null的课程，以后可以考虑分出去用一个单独的文件存放
        HashMap<String, String> map = new HashMap<>();
        map.put("MY220040", "毛泽东思想和中国特色社会主义理论体系概论");
        map.put("MY220030", "习近平新时代中国特色社会主义思想概论");

        boolean flag = false;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            Object jw_course_name = o.get("jw_course_name");
            if(jw_course_name == null) {
                o.put("jw_course_name", map.get(o.getString("jw_course_code")));
                jsonArray.set(jsonArray.indexOf(o), o);
                flag = true;
            }
        }
        return flag;
    }
}
