package org.shirakawatyu.handixikebackend.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.shirakawatyu.handixikebackend.common.Result;
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

    @GetMapping("/api/v2/extension/library")
    public Result getLibraryInfo(HttpSession session) throws IOException{
        return libraryService.getLibrary (session);
    }

    @GetMapping("/api/v2/extension/queryBooks")
    public Result queryBooks(HttpSession session, @RequestParam("bookName")String bookName, @RequestParam("page")int page) throws IOException {

        return libraryService.queryBooks(session,bookName,page);
    }

    @GetMapping("/api/v2/extension/queryLocation")
    public Result queryLocation(HttpSession session, @RequestParam("id")String id) throws IOException {
        return libraryService.queryLocation(session,id);

    }


}
