package com.jeff.dsource.rxrequest.exception;

/**
 * @author Jeff
 * @date 2020/6/15
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class BusinessException extends Exception {

    public BusinessException(String message) {
        super(new Throwable("Business error: " + message));
    }
}
