package org.shirakawatyu.handixikebackend.pojo;

import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonMessage {
    long studentId;
    long qq;
    JSONArray lessons;
    long term;
}
