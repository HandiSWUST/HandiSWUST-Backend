package org.shirakawatyu.handixikebackend.service.impl;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.pojo.Library;
import org.shirakawatyu.handixikebackend.service.LibraryService;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LibraryServiceImpl implements LibraryService {

    @Override
    public Result getLibrary(HttpSession session) {
        BasicCookieStore cookieStore = (BasicCookieStore)session.getAttribute("cookieStore");
        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");
            try {
                Requests.get("http://202.115.162.45:8080/opac/search_adv.php", "", restTemplate);
                List<Cookie> cookiesStore = cookieStore.getCookies();
                for (Cookie cookie : cookiesStore) {
                    if (cookie.getName().equals("PHPSESSID")) {
                        session.setAttribute("token", new String[]{"PHPSESSID", cookie.getValue()});
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
                  return Result.ok().data(books);
            } catch (Exception e){
                e.printStackTrace();
            }

            return Result.fail();
    }

    @Override
    public Result queryBooks(HttpSession session, String bookName, int page) throws IOException {
        String[] token = (String[])session.getAttribute("token");
        String requestBody = "{\"searchWords\":[{\"fieldList\":[{\"fieldCode\":\"\",\"fieldValue\":\""+bookName+"\"}]}],\"filters\":[],\"limiter\":[],\"sortField\":\"relevance\",\"sortType\":\"desc\",\"pageSize\":20,\"pageCount\":"+page+",\"locale\":\"\",\"first\":true}";
        String url = "http://202.115.162.45:8080/opac/ajax_search_adv.php";
        Document post = Jsoup.connect(url).header("Content-Type", "application/json").requestBody(requestBody).cookie(token[0], token[1]).post();
        return Result.ok().data(post.text());
    }

    @Override
    public Result queryLocation(HttpSession session, String id) throws IOException {
        String[] token = (String[])session.getAttribute("token");
        String url = "http://202.115.162.45:8080/opac/ajax_item.php?marc_no="+id;
        Document document = Jsoup.connect(url).cookie(token[0], token[1]).get();

        return Result.ok().data(document.body().html());
    }
}