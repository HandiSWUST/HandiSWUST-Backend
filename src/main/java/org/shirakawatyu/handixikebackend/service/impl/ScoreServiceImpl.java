package org.shirakawatyu.handixikebackend.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import org.shirakawatyu.handixikebackend.service.ScoreService;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    RestTemplate restTemplate;





    public String setTerm(String term){

        String year = term.substring(0,2);
        int i = Integer.parseInt(year) + 1;
        String nextYear = Integer.toString(i);
        String th = term.substring(2);
        return year+"-"+nextYear+" "+"第"+th+"学期";


    }
    @Override
    public String getScore(List<String> cookies,HttpSession session) {
        restTemplate = (RestTemplate) session.getAttribute("template");

        ResponseEntity<String> entity0 = Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage#view_index", "", cookies, restTemplate);
        ResponseEntity<String> entity = Requests.get("http://myo.swust.edu.cn/mht_shall/a/service/studentMark", "http://myo.swust.edu.cn/mht_shall/a/service/serviceFrontManage#view_index", cookies, restTemplate);

        JSONObject jsonObject = JSONObject.parseObject(entity.getBody());
        HashMap<String, ArrayList<Object>>map = new HashMap<>();
        JSONObject body = (JSONObject)jsonObject.get("body");
        if(body == null) {
            return "";
        }
        String result = body.get("result").toString();
        JSONArray jsonArray = JSONObject.parseArray(result);
        for(Object s : jsonArray.toArray()){
            JSONObject json = (JSONObject) s;
            if(map.get(setTerm((String)json.get("term")))==null){
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(s);
                map.put(setTerm((String)json.get("term")),objects);}
            else {
                ArrayList<Object> term = map.get(setTerm((String)json.get("term")));
                term.add(s);
                map.put(setTerm((String)json.get("term")), term);
            }
        }
//        System.out.println(map);
        return JSONObject.toJSONString(map);
    }
}