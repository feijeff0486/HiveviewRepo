package com.jeff.dsource.rxrequest.interceptor;

import android.util.Log;

import com.jeff.jframework.tools.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求级缓存
 * 在没有有网络下的缓存拦截器
 * 使用方式：{@link okhttp3.OkHttpClient.Builder#addInterceptor(Interceptor)}
 *
 * @see CacheWithNetInterceptor
 *
 * 至于为什么将有网无网缓存分开实现，可参考这里：https://www.jianshu.com/p/cf59500990c7
 * <p>
 *
 * @author Jeff
 * @date 2020/6/12
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class CacheWithoutNetInterceptor implements Interceptor {
    private static final String TAG = "CacheNoNetInterceptor";
    /**
     * set cache times is 3 days
     */
    private static final int MAX_STALE=60 * 60 * 24 * 3;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean connected = NetworkUtils.isConnected();
        if (!connected) {
            Log.d(TAG, "intercept: ");
            //如果没有网络，则启用 FORCE_CACHE
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE)
                    .build();
        }
        //有网络的时候，这个拦截器不做处理，直接返回
        return chain.proceed(request);
    }
}
