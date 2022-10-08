package org.shirakawatyu.handixikebackend.utils;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 封装的发送请求
 * @author ShirakawaTyu
 * @date: 2022/10/1 17:45
 */
public class Requests {
    /**
     * 本方法用于发起get请求，本质上是对RestTemplate的封装
     * <p>
     * 使用时，传入需要请求的url，referer(来源，为了反反爬虫，如果不需要用到请传入""字符串)，cookies，restTemplate(在外部Autowire一个传进来就行)。
     * 返回一个ResponseEntity<String>对象，通过这个对象可以获得需要的数据
     *
     * @param url
     * @param referer
     * @param cookies
     * @param restTemplate
     * @return get1
     */
    public static ResponseEntity<String> get(String url, String referer, List<String> cookies, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
//        headers.put(HttpHeaders.COOKIE, cookies);
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(!"".equals(referer)) {
            headers.set("referer", referer);
        }
        MultiValueMap<String, String> map1= new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> httpEntity1 = new HttpEntity<>(map1, headers);

        ResponseEntity<String> get1 = restTemplate.exchange(url, HttpMethod.GET, httpEntity1, String.class);
//        List<String> strings = get1.getHeaders().get("Set-Cookie");
//        if(strings != null) {
//            for (int j = 0; j < strings.size(); j++) {
//                int size = cookies.size();
//                String s = strings.get(j);
//                for (int i = 0; i < size; i++) {
//                    if (cookies.get(i).contains(s.substring(0, s.indexOf("=")))) {
//                        try {
//
//                            cookies.remove(i);
//                        }catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        size--;
//                    }
//                }
//                try{
//                    cookies.add(s);
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if(get1.getStatusCode().value() == 302) {
//            return get(url.substring(0, url.indexOf("/", 7)) + get1.getHeaders().getLocation(), "", cookies, restTemplate);
//        }
        return get1;
    }

    /**
     * 本方法用于发起post请求，本质上是对RestTemplate的封装
     * <p>
     * 使用时，传入需要请求的url，cookies，data(需要携带的表单参数，如果没参数请传一个空的MultiValueMap)，restTemplate(在外部Autowire一个传进来就行)。
     * 返回一个ResponseEntity<String>对象，通过这个对象可以获得需要的数据
     *
     * @param url
     * @param data
     * @param cookies
     * @param restTemplate
     * @return entity
     */
    public static ResponseEntity<String> post(String url, List<String> cookies, MultiValueMap<String, String> data, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
//        headers.put(HttpHeaders.COOKIE, cookies);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(data, headers);
        ResponseEntity<String> entity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
//        if (entity.getHeaders().get("Set-Cookie") != null) {
//            ArrayList<String> strings = new ArrayList<>();
//            for (String s :
//                    cookies) {
//                strings.add(s);
//            }
//            cookies = strings;
//            List<String> strings1 = entity.getHeaders().get("Set-Cookie");
//            if(strings1 != null) {
//                for (String s : strings1) {
//                    for (String i : cookies) {
//                        if (i.contains(s.substring(0, s.indexOf("=")))) {
//                            cookies.remove(i);
//                        }
//                    }
//                    cookies.add(s);
//                }
//            }
//        }
//        if(entity.getStatusCode().value() == 302) {
//            return get(url.substring(0, url.indexOf("/", 7)) + entity.getHeaders().getLocation(), "", cookies, restTemplate);
//        }
        return entity;
    }

}
