package org.shirakawatyu.handixikebackend.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.shirakawatyu.handixikebackend.service.LibraryService;
import org.shirakawatyu.handixikebackend.utils.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class LibraryController {
    @Autowired
    LibraryService libraryService;

    @GetMapping("/api/library")
    public String getLibraryInfo(HttpSession session) throws IOException{
        return libraryService.getLibrary (session);
    }

    @GetMapping("/api/queryBooks")
    public String queryBooks(HttpSession session, @RequestParam("bookName")String bookName, @RequestParam("page")int page) throws IOException {

        return libraryService.queryBooks(session,bookName,page);
    }

    @GetMapping("/api/queryLocation")
    public String queryLocation(HttpSession session,@RequestParam("id")String id) throws IOException {
        return libraryService.queryLocation(session,id);

    }


}
