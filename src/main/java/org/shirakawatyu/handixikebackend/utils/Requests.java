package org.shirakawatyu.handixikebackend.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 封装的发送请求
 * @author ShirakawaTyu
 * @since 2022/10/1 17:45
 */
public class Requests {
    /**
     * 本方法用于获取session中的restTemplate
     *
     * @param session tomcat的session实例

     * @return restTemplate
     */
    public static RestTemplate getRestTemplate(HttpSession session){
        return (RestTemplate)session.getAttribute("template");
    }
    /**
     * 本方法用于发起get请求，本质上是对RestTemplate的封装
     * <p>
     * 使用时，传入需要请求的url，referer(来源，为了反反爬虫，如果不需要用到请传入""字符串)，cookies，restTemplate(在外部Autowire一个传进来就行)。
     * 返回一个ResponseEntity<String>对象，通过这个对象可以获得需要的数据
     *
     * @param url 请求url
     * @param referer 来源
     * @param restTemplate RestTemplate对象
     * @return get1
     */

    public static ResponseEntity<String> get(String url, String referer, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(!"".equals(referer)) {
            headers.set("referer", referer);
        }
        MultiValueMap<String, String> map1= new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> httpEntity1 = new HttpEntity<>(map1, headers);

        return restTemplate.exchange(url, HttpMethod.GET, httpEntity1, String.class);
    }

    /**
     * 本方法用于发起post请求，本质上是对RestTemplate的封装
     * <p>
     * 使用时，传入需要请求的url，cookies，data(需要携带的表单参数，如果没参数请传一个空的MultiValueMap)，restTemplate(在外部Autowire一个传进来就行)。
     * 返回一个ResponseEntity<String>对象，通过这个对象可以获得需要的数据
     *
     * @param url 请求url
     * @param data 请求参数
     * @param restTemplate RestTemplate对象
     * @return entity
     */
    public static ResponseEntity<String> post(String url, MultiValueMap<String, String> data, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(data, headers);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }

}
