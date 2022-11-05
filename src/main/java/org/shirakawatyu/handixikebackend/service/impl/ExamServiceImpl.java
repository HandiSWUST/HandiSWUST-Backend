package org.shirakawatyu.handixikebackend.service.impl;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.pojo.Exam;
import org.shirakawatyu.handixikebackend.pojo.Library;
import org.shirakawatyu.handixikebackend.service.ExamService;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;

@Service
public class ExamServiceImpl implements ExamService {
    private static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private List<Exam> setExamList(String [] strings){
        ArrayList<Exam> exams = new ArrayList<>();

        int examNum = (strings.length-8)/9;
        for(int p =0;p<examNum;p++){
            int aid = 8+p*9;
            Exam exam = new Exam(strings[aid],strings[1+aid],strings[2+aid],strings[3+aid],strings[4+aid],strings[5+aid],strings[6+aid],strings[7+aid],strings[8+aid]);
            exams.add(exam);
        }

     return  exams;
    }


    @Override
    public String getExam(HttpSession session) {
        trustEveryone();
        Connection conn = Jsoup.connect("http://cas.swust.edu.cn/authserver/login?service=https%3A%2F%2Fmatrix%2Edean%2Eswust%2Eedu%2Ecn%2FacadmicManager%2Findex%2Ecfm%3Fevent%3DstudentPortal%3ADEFAULT%5FEVENT");
//        conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        conn.header("Accept-Encoding", "gzip, deflate, br");
//        conn.header("Accept-Language", "zh-CN,zh;q=0.9");
//        conn.header("Cache-Control", "max-age=0");
//        conn.header("Connection", "keep-alive");
//        conn.header("Host", "blog.maxleap.cn");
//        conn.header("Upgrade-Insecure-Requests", "1");
//        conn.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");

        BasicCookieStore cookieStore = (BasicCookieStore)session.getAttribute("cookieStore");


        Cookie Tgc = cookieStore.getCookies().get(2);
        List<Cookie> cookiesStore = cookieStore.getCookies();
        Map<String, String> map = new HashMap<>();
        for (Cookie cookie : cookiesStore) {
            map.put(cookie.getName(), cookie.getValue());
        }

//        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");

        try {
            Connection.Response execute = conn.method(Connection.Method.GET).cookies(map).execute();
            String sso = execute.cookie("SSO");
            Document doc = Jsoup.connect("https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:examTable").cookie("SSO", sso).get();
            String info = doc.body().getElementsByTag("td").text();
            String[] s = info.split(" ");
            System.out.println(Arrays.toString(s));
            List<Exam> exams = setExamList(s);
            return JSONObject.toJSONString(exams);


        }catch (IOException e){
            e.printStackTrace();
        }

        return "s";

    }


}
