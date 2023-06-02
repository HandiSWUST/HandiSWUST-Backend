package org.shirakawatyu.handixikebackend.service.impl;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.shirakawatyu.handixikebackend.api.LibraryApi;
import org.shirakawatyu.handixikebackend.common.Result;
import org.shirakawatyu.handixikebackend.pojo.Library;
import org.shirakawatyu.handixikebackend.service.LibraryService;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LibraryServiceImpl implements LibraryService {
    @Resource(name = "libraryApi")
    LibraryApi libraryApi;

    @Override
    public Result getLibrary(HttpSession session) {
        BasicCookieStore cookieStore = (BasicCookieStore)session.getAttribute("cookieStore");
        RestTemplate restTemplate = (RestTemplate) session.getAttribute("template");
        return Result.ok().data(libraryApi.getBorrows(restTemplate,cookieStore,session));
    }

    @Override
    public Result queryBooks(HttpSession session, String bookName, int page) throws IOException {
        return Result.ok().data(libraryApi.queryBooks(session,bookName,page));
    }

    @Override
    public Result queryLocation(HttpSession session, String id) throws IOException {
        return Result.ok().data(libraryApi.getLocationOfBook(session, id));
    }
}