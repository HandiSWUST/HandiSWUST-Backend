package org.shirakawatyu.handixikebackend.api.impl;

import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.api.LibraryApi;

import org.shirakawatyu.handixikebackend.pojo.Library;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("libraryApi")
public class LibraryApiImpl implements LibraryApi {

    @Override
    public ArrayList<Library> getBorrows(RestTemplate restTemplate, BasicCookieStore cookieStore, HttpSession session) {

        ArrayList<Library> books = new ArrayList<>();
        Logger logger = Logger.getLogger("LibraryApi.getBorrow");
        try {
            Requests.get("http://202.115.162.45:8080/opac/search_adv.php", "", restTemplate);
            List<Cookie> cookiesStore = cookieStore.getCookies();
            for (Cookie cookie : cookiesStore) {
                if ("PHPSESSID".equals(cookie.getName())) {
                    session.setAttribute("token", new String[]{"PHPSESSID", cookie.getValue()});//用于以后查询图书
                }
            }
            Requests.get("http://202.115.162.45:8080/reader/login.php", "http://202.115.162.45:8080/opac/search_adv.php", restTemplate);
            Requests.get("http://202.115.162.45:8080/reader/book_lst.php", "", restTemplate);
            ResponseEntity<String> entity = Requests.get("http://202.115.162.45:8080/reader/book_lst.php", "", restTemplate);//请求两遍才能进去


            String body = entity.getBody();
            Document parse = Jsoup.parse(body);
            Elements tr = parse.getElementsByTag("tr");


            String text = tr.text();
            String[] s = text.split(" ");
            System.out.println(Arrays.toString(s));


            Library library = new Library();
            for(int i =10;i<s.length-3;i++){
                if(s[i].contains("/")){
                    library.setBookName(s[i-1]);
                }

                if(s[i].contains("著")||s[i].contains("主编")){
                    while(!s[i+1].contains("-")) {
                        i++;
                    }
                    library.setBorrowTime(s[i+1]);
                    library.setExpire(s[i+2]);
                    library.setLocation(s[i+4]);
                    books.add(new Library(library));
                    library = new Library();
                }
            }
        } catch (Exception e){
            logger.log(Level.WARNING,"查询借阅图书错误，有可能是第一次官网验证不通过，比较玄学，如果该报错出现好几次就是接口寄了");
            logger.log(Level.WARNING, books.toString());
            throw e;
        }
        return books;
    }

    @Override
    public String queryBooks(HttpSession session,String bookName,int page) throws IOException {
        String[] token = (String[])session.getAttribute("token");
        String requestBody = "{\"searchWords\":[{\"fieldList\":[{\"fieldCode\":\"\",\"fieldValue\":\""+bookName+"\"}]}],\"filters\":[],\"limiter\":[],\"sortField\":\"relevance\",\"sortType\":\"desc\",\"pageSize\":20,\"pageCount\":"+page+",\"locale\":\"\",\"first\":true}";
        String url = "http://202.115.162.45:8080/opac/ajax_search_adv.php";
        Document post = Jsoup.connect(url).header("Content-Type", "application/json").requestBody(requestBody).cookie(token[0], token[1]).post();
        return post.text();
    }

    @Override
    public String getLocationOfBook(HttpSession session,String id) throws IOException {
        String[] token = (String[])session.getAttribute("token");
        String url = "http://202.115.162.45:8080/opac/ajax_item.php?marc_no="+id;
        Document document = Jsoup.connect(url).cookie(token[0], token[1]).get();
        return document.body().html();
    }
}
