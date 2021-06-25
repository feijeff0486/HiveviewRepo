package com.jeff.dsource.rxrequest.exception;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/6/15
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class ResponseException extends Exception {
    public int code;
    public String message;

    public ResponseException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }
}
