package org.shirakawatyu.handixikebackend.service;

import org.apache.http.client.CircularRedirectException;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface ScoreService {
    public String getScore(HttpSession session) throws CircularRedirectException;

    public String getGPA(HttpSession session)throws CircularRedirectException;
}
