package org.shirakawatyu.handixikebackend.service;

import org.apache.http.client.CircularRedirectException;
import org.shirakawatyu.handixikebackend.common.Result;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface ScoreService {
    Result getScore(HttpSession session) throws CircularRedirectException;

    Result getGPA(HttpSession session)throws CircularRedirectException;
}
