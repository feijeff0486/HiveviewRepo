package com.jeff.dsource.rxrequest.okhttp;

import android.support.annotation.Nullable;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/7/23
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class OkHttpClientManager {
    private static volatile OkHttpClientManager sInstance;
    private OkHttpClientFactory okHttpClientFactory;

    private OkHttpClientManager() {
        okHttpClientFactory = new OkHttpClientFactory();
    }

    public static OkHttpClientManager getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpClientManager();
                }
            }
        }
        return sInstance;
    }

    public OkHttpClientFactory getOkHttpClientFactory() {
        return okHttpClientFactory;
    }

    public OkHttpClient getImageOkHttpClient(@Nullable String key, Interceptor... interceptors) {
        return okHttpClientFactory.getImageOkHttpClient(key, interceptors);
    }

    public OkHttpClient getOkHttpClient(@Nullable String key, Interceptor... interceptors) {
        return okHttpClientFactory.getOkHttpClient(key, interceptors);
    }
}
