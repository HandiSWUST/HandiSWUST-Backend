package org.shirakawatyu.handixikebackend.common;

/**
 * @author ShirakawaTyu
 */
public interface ResultCode {
    // 认证相关
    int LOGIN_FAIL = 1500;
    int REMOTE_SERVICE_ERROR = 1502;
    int LOGIN_SUCCESS = 1200;
    int LOGOUT_SUCCESS = 2200;
    int HAS_LOGIN = 3200;
    int LOGOUT = 3401;
    int TIMEOUT = 5501;
    int SERVER_CLOSE = 5502;
}
