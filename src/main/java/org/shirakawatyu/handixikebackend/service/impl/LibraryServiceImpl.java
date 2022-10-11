package org.shirakawatyu.handixikebackend.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.pojo.Library;
import org.shirakawatyu.handixikebackend.service.LibraryService;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryServiceImpl implements LibraryService {

//    RestTemplate restTemplate;

    @Override
    public String getLibrary(HttpSession session)  throws IOException{
        BasicCookieStore cookieStore = (BasicCookieStore)session.getAttribute("cookieStore");
        Cookie Tgc = cookieStore.getCookies().get(2);
        List<Cookie> cookiesStore = cookieStore.getCookies();
        Map<String, String> map = new HashMap<>();
        for (Cookie cookie : cookiesStore) {
           map.put(cookie.getName(), cookie.getValue());
        }



        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");

            try {
                Connection.Response execute = Jsoup.connect("http://cas.swust.edu.cn/authserver/login?service=http://202.115.162.45:8080/reader/hwthau.php").followRedirects(false).cookies(map).execute();

//                System.out.println(execute.headers());
                List<String> locations = execute.headers("Location");
                Connection.Response execute1 = Jsoup.connect(locations.get(0)).followRedirects(false).cookies(map).execute();
                List<String> headers = execute1.headers("Set-Cookie");
            String[] split = headers.get(0).split("=");
            map.put(split[0],split[1]);
            Connection.Response execute2 = Jsoup.connect("http://202.115.162.45:8080/reader/book_lst.php").followRedirects(false).cookies(map).execute();
                Requests.get("http://202.115.162.45:8080/reader/book_lst.php", "",restTemplate);
                ResponseEntity<String> entity = Requests.get("http://202.115.162.45:8080/reader/book_lst.php", "", restTemplate);//请求两遍才能进去


                String body = entity.getBody();
                Document parse = Jsoup.parse(body);
                Elements tr = parse.getElementsByTag("tr");


                String text = tr.text();
                String[] s = text.split(" ");
//                System.out.println(Arrays.toString(s)); //9:书名  -4：地址 -6 应还日期 -7 借出日期 /~著



                ArrayList<Library> books = new ArrayList<>();
                Library library = new Library();
                for(int i =10;i<s.length-3;i++){
                    if(s[i].contains("/")){
                        library.setBookName(s[i-1]);

                    }

                    if(s[i].contains("著")){
                        library.setBorrowTime(s[i+1]);
                        library.setExpire(s[i+2]);
                        library.setLocation(s[i+4]);
                        books.add(new Library(library));
                        library = new Library();
                    }

                }
//                System.out.println(books);
                return JSONObject.toJSONString(books);


            }catch (IOException e){
                e.printStackTrace();
            }

            return "undefined";







    }
}