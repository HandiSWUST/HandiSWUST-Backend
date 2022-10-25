package org.shirakawatyu.handixikebackend.controller;

import cn.hutool.http.HttpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HitokotoController {

    @GetMapping("/api/gethitokoto")
    @ResponseBody
    public String getSentence(@RequestParam String c, @RequestParam String encode) {
        return HttpUtil.get("https://v1.hitokoto.cn?c=" + c + "&" + encode);
    }
}
