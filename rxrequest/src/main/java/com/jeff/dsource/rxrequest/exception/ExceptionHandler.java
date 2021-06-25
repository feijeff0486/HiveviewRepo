package com.jeff.dsource.rxrequest.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.jeff.dsource.rxrequest.Status;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.EOFException;
import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * 转换处理请求异常
 *
 * @author Jeff
 * @date 2020/6/15
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class ExceptionHandler {

    public static ResponseException handleException(Throwable e) {
        if (e==null){
            return new ResponseException(e, Status.Error.UNKNOWN);
        }
        ResponseException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseException(e, Status.Error.HTTP_ERROR);
            switch (httpException.code()) {
                case Status.CODE_401:
                    ex.message = "操作未授权";
                    break;
                case Status.CODE_403:
                    ex.message = "请求被拒绝";
                    break;
                case Status.CODE_404:
                    ex.message = "资源不存在";
                    break;
                case Status.CODE_408:
                    ex.message = "服务器执行超时";
                    break;
                case Status.CODE_500:
                    ex.message = "服务器内部错误";
                    break;
                case Status.CODE_503:
                    ex.message = "服务器不可用";
                    break;
                default:
                    ex.message = "网络不给力，请您检查网络";
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof MalformedJsonException
                || e instanceof EOFException) {
            ex = new ResponseException(e, Status.Error.PARSE_ERROR);
            ex.message = "数据解析错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponseException(e, Status.Error.NETWORD_ERROR);
            ex.message = "网络不给力，请您检查网络";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLException) {
            ex = new ResponseException(e, Status.Error.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponseException(e, Status.Error.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseException(e, Status.Error.TIMEOUT_ERROR);
            ex.message = "服务器响应超时";
            return ex;
        } else if (e instanceof java.net.UnknownHostException) {
            ex = new ResponseException(e, Status.Error.TIMEOUT_ERROR);
            ex.message = "域名解析错误";
            return ex;
        }else if(e instanceof ResponseUnavailableException){
            ex = new ResponseException(e, Status.Error.RESPONSE_ERROR);
            ex.message = "结果不可用";
            return ex;
        }else if(e instanceof BusinessException){
            ex = new ResponseException(e, Status.Error.BUSINESS_ERROR);
            ex.message = "业务错误";
            return ex;
        }else {
            ex = new ResponseException(e, Status.Error.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }
    }

}
