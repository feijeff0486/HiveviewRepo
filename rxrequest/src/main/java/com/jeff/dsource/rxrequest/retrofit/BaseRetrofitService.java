package com.jeff.dsource.rxrequest.retrofit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jeff.dsource.rxrequest.okhttp.OkHttpClientManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 由外部继承
 *
 * @author Jeff
 * @describe
 * @date 2019/5/30.
 */
public abstract class BaseRetrofitService {
    private Converter.Factory gsonConverterFactory;
    private Converter.Factory scalarsConverterFactory;
    private CallAdapter.Factory rxJavaCallAdapterFactory;

    public <T> T createApi(String baseUrl, final Class<T> serviceClazz) {
        return createApi("default", baseUrl, serviceClazz, getGsonConverterFactory());
    }

    public <T> T createApi(String key, String baseUrl, final Class<T> serviceClazz,
                           @NonNull Converter.Factory... factories) {
        return createApi(getOkHttpClient(key), baseUrl, serviceClazz, factories);
    }

    public <T> T createApi(OkHttpClient okHttpClient, String baseUrl,
                           final Class<T> serviceClazz, @NonNull Converter.Factory... factories) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(okHttpClient).baseUrl(baseUrl)
                .addConverterFactory(new NullOnEmptyConverterFactory());
        if (factories != null && factories.length > 0) {
            for (Converter.Factory factory : factories) {
                builder.addConverterFactory(factory);
            }
        }
        Retrofit retrofit = builder.addCallAdapterFactory(getRxJavaCallAdapterFactory()).build();
        return retrofit.create(serviceClazz);
    }

    public synchronized OkHttpClient getOkHttpClient(@Nullable String key, Interceptor... interceptors) {
        return OkHttpClientManager.getInstance().getOkHttpClient(key, interceptors);
    }

    protected Converter.Factory getGsonConverterFactory() {
        if (gsonConverterFactory == null) {
            gsonConverterFactory = GsonConverterFactory.create();
        }
        return gsonConverterFactory;
    }

    protected Converter.Factory getScalarsConverterFactory() {
        if (scalarsConverterFactory == null) {
            scalarsConverterFactory = ScalarsConverterFactory.create();
        }
        return scalarsConverterFactory;
    }

    protected CallAdapter.Factory getRxJavaCallAdapterFactory() {
        if (rxJavaCallAdapterFactory == null) {
            rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
        }
        return rxJavaCallAdapterFactory;
    }
}
