package org.shirakawatyu.handixikebackend.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.shirakawatyu.handixikebackend.common.Const;
import org.shirakawatyu.handixikebackend.utils.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ShirakawaTyu
 */
@RestController
public class InfoController {
    @GetMapping("/api/week")
    public String getWeek() {
        JSONObject week = new JSONObject();
        week.put("cur", DateUtil.curWeek());
        week.put("total", DateUtil.totalWeek());
        week.put("startDate", Long.toString(Const.START_DATE));
        return JSON.toJSONString(week);
    }

    @GetMapping("/api/web/version")
    public String getVersion() {
        return Const.WEB_VERSION;
    }
}
