package org.shirakawatyu.handixikebackend.service;

import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.CircularRedirectException;
import org.shirakawatyu.handixikebackend.common.Result;

public interface ScoreService {
    Result getScore(HttpSession session) throws CircularRedirectException;

    Result getGPA(HttpSession session)throws CircularRedirectException;
}
