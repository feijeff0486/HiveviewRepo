package com.jeff.dsource.rxrequest.interceptor;

import android.util.Log;

import com.jeff.jframework.tools.NetworkUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求级缓存
 * 在有网络下的缓存拦截器
 * 使用方式：{@link okhttp3.OkHttpClient.Builder#addNetworkInterceptor(Interceptor)}
 *
 * @see CacheWithoutNetInterceptor
 * 至于为什么将有网无网缓存分开实现，可参考这里：https://www.jianshu.com/p/cf59500990c7
 * <p>
 * @author Jeff
 * @date 2020/6/12
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class CacheWithNetInterceptor implements Interceptor {
    private static final String TAG = "CacheWithNetInterceptor";
    /**
     * read from cache for 60 s
     */
    private static final int MAX_STALE=60;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean connected = NetworkUtils.isConnected();
        if(connected){
            Log.d(TAG, "intercept: ");
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + MAX_STALE)
                    .build();
        }
        //如果没有网络，不做处理，直接返回
        return chain.proceed(request);
    }
}
