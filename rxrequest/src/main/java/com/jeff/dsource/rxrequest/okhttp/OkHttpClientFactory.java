package com.jeff.dsource.rxrequest.okhttp;

import android.support.annotation.Nullable;

import com.jeff.dsource.rxrequest.RxRequest;
import com.jeff.dsource.rxrequest.cache.CacheManager;
import com.jeff.dsource.rxrequest.interceptor.CacheWithNetInterceptor;
import com.jeff.dsource.rxrequest.interceptor.CacheWithoutNetInterceptor;
import com.jeff.dsource.rxrequest.interceptor.logging.LoggingInterceptor;
import com.jeff.dsource.rxrequest.util.HttpsUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;

/**
 * OkHttpClient工厂
 *
 * @author Jeff
 * @date 2020/6/12
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class OkHttpClientFactory {

    /**
     * 读取超时时间
     */
    private static final int DEFAULT_READ_TIMEOUT = 20;
    /**
     * 链接超时时间
     */
    private static final int DEFAULT_CONN_TIMEOUT = 15;
    /**
     * 上传超时时间
     */
    private static final int DEFAULT_WRITE_TIMEOUT = 20;

    private final ConcurrentHashMap<String, OkHttpClient> mClientMap = new ConcurrentHashMap<>();

    public OkHttpClient.Builder defaultBuilder(final boolean logging, final Interceptor... interceptors) {
        return commonBuilder(logging, false, interceptors);
    }

    public OkHttpClient.Builder commonBuilder(final boolean logging, final boolean image, final Interceptor... interceptors) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//                .cookieJar(new CookieJarImpl(new PersistentCookieStore()))
        if (!image) {
            builder.cache(CacheManager.getInstance().getCache())
                    //将无网络拦截器添加为应用拦截器
                    .addInterceptor(new CacheWithoutNetInterceptor())
                    //将有网络拦截器添加为网络拦截器
                    .addNetworkInterceptor(new CacheWithNetInterceptor());
        }
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        if (!image) {
            //添加拦截器
            builder.addInterceptor(new LoggingInterceptor.Builder()
                    // 是否开启日志打印
                    .loggable(logging)
                    // 打印的等级
                    .setLevel(RxRequest.getInstance().getLogLevel())
                    // 打印类型
                    .log(Platform.INFO)
                    // request的Tag
                    .request("Ok-Request")
                    // Response的Tag
                    .response("Ok-Response")
                    // 添加打印头, 注意 key 和 value 都不能是中文
                    .addHeader("log-header", "I am the log request header.")
                    .build());
        }
        //连接超时时间
        builder.connectTimeout(DEFAULT_CONN_TIMEOUT, TimeUnit.SECONDS)
                //读取超时时间设置
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                //写入超时时间设置
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为15s(可显著提升网络请求耗时)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                //错误重连
                .retryOnConnectionFailure(true)
                //无条件信任所有证书
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
        return builder;
    }

    /**
     * 加载图片时使用
     *
     * @param key          client的key，根据key先查找，若没有则创建
     * @param interceptors 网络拦截器
     * @return
     */
    public OkHttpClient getImageOkHttpClient(@Nullable String key, Interceptor... interceptors) {
        synchronized (mClientMap) {
            OkHttpClient okHttpClient;
            if (mClientMap.get(key) == null) {
                okHttpClient = commonBuilder(true, true, interceptors).build();
                mClientMap.put(key, okHttpClient);
                return okHttpClient;
            }
            return mClientMap.get(key);
        }
    }

    /**
     * 普通网络请求时使用
     *
     * @param key          client的key，根据key先查找，若没有则创建
     * @param interceptors 网络拦截器
     * @return
     */
    public OkHttpClient getOkHttpClient(@Nullable String key, Interceptor... interceptors) {
        synchronized (mClientMap) {
            OkHttpClient okHttpClient;
            if (mClientMap.get(key) == null) {
                okHttpClient = defaultBuilder(true, interceptors).build();
                mClientMap.put(key, okHttpClient);
                return okHttpClient;
            }
            return mClientMap.get(key);
        }
    }
}
