package org.shirakawatyu.handixikebackend.service;

import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.handixikebackend.common.Result;

import java.io.IOException;

public interface LibraryService {
    Result getLibrary(HttpSession session) throws IOException;

    Result queryBooks(HttpSession session,String bookName,int page) throws IOException;

    Result queryLocation(HttpSession session, String id) throws IOException;
}
