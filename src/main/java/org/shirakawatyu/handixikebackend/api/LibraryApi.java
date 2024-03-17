package org.shirakawatyu.handixikebackend.api;

import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.shirakawatyu.handixikebackend.pojo.Library;

import java.io.IOException;
import java.util.ArrayList;

public interface LibraryApi {
    ArrayList<Library> getBorrows(BasicCookieStore cookieStore, HttpSession session);
    String queryBooks(HttpSession session,String bookName,int page) throws IOException;
    String getLocationOfBook(HttpSession session,String id) throws IOException;
}
