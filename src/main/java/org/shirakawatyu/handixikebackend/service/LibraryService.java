package org.shirakawatyu.handixikebackend.service;

import org.shirakawatyu.handixikebackend.common.Result;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public interface LibraryService {
    Result getLibrary(HttpSession session) throws IOException;

    Result queryBooks(HttpSession session,String bookName,int page) throws IOException;

    Result queryLocation(HttpSession session, String id) throws IOException;
}
