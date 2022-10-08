package org.shirakawatyu.handixikebackend.service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public interface LibraryService {
    public String getLibrary(HttpSession session) throws IOException;
}
