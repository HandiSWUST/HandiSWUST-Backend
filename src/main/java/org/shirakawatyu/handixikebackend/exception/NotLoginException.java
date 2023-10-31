package org.shirakawatyu.handixikebackend.exception;

/**
 * @author ShirakawaTyu
 */
public class NotLoginException extends RuntimeException {
    public NotLoginException() {
    }

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLoginException(Throwable cause) {
        super(cause);
    }
}
