package com.jeff.dsource.rxrequest.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.jeff.jframework.tools.GsonUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 * 基于OkHttp封装的网络请求工具
 *
 * @author Jeff
 * @date 2020/7/30
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class OkRequest {
    private static final String TAG = "OkRequest";
    private static volatile OkRequest sInstance;
    private OkHttpClient okHttpClient;
    private Executor mDeliver;

    private OkRequest() {
        okHttpClient = OkHttpClientManager.getInstance().getOkHttpClient(TAG);
    }

    public static OkRequest getInstance() {
        if (sInstance == null) {
            synchronized (OkRequest.class) {
                if (sInstance == null) {
                    sInstance = new OkRequest();
                }
            }
        }
        return sInstance;
    }

    /**
     * get request
     *
     * @param url
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public Observable<Call> get(String url) {
        return Observable.just(url)
                .map(new Function<String, Call>() {
                    @Override
                    public Call apply(String url) throws Exception {
                        if (okHttpClient == null) {
                            okHttpClient = OkHttpClientManager.getInstance().getOkHttpClient(TAG);
                        }
                        //创建Request
                        Request request = new Request.Builder().url(url).build();
                        //得到Call对象
                        return okHttpClient.newCall(request);
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * get request
     *
     * @param url
     * @param observer
     */
    public <T> void get(String url, OkRequestObserver<T> observer) {
        get(url).subscribe(observer);
    }

    /**
     * get request async
     *
     * @param url
     */
    public void getAsync(String url, final Callback callback) {
        get(url).map(new Function<Call, Void>() {
            @Override
            public Void apply(Call call) throws Exception {
                call.enqueue(callback);
                return null;
            }
        }).subscribe();
    }

    /**
     * get request sync
     *
     * @param url
     */
    public Observable<Response> getSync(String url) {
        return get(url).map(new Function<Call, Response>() {
            @Override
            public Response apply(Call call) throws Exception {
                return call.execute();
            }
        });
    }

    /**
     * post request
     *
     * @param url
     * @param params
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public Observable<Call> post(String url, final Map<String, String> params) {
        return Observable.just(url)
                .map(new Function<String, Call>() {
                    @Override
                    public Call apply(String url) throws Exception {
                        if (okHttpClient == null) {
                            okHttpClient = OkHttpClientManager.getInstance().getOkHttpClient(TAG);
                        }
                        FormBody.Builder builder = new FormBody.Builder();
                        //遍历参数集合
                        for (String key : params.keySet()) {
                            builder.add(key, params.get(key));
                        }

                        //创建Request
                        Request request = new Request.Builder().url(url).post(builder.build()).build();
                        return okHttpClient.newCall(request);
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * post request
     *
     * @param url
     * @param params
     * @param observer
     */
    public <T> void post(String url, Map<String, String> params, OkRequestObserver<T> observer) {
        post(url, params).subscribe(observer);
    }

    /**
     * post request async
     *
     * @param url
     * @param params
     */
    public void postAsync(String url, Map<String, String> params, final Callback callback) {
        post(url,params).map(new Function<Call, Void>() {
            @Override
            public Void apply(Call call) throws Exception {
                call.enqueue(callback);
                return null;
            }
        }).subscribe();
    }

    /**
     * post request sync
     *
     * @param url
     * @param params
     */
    public Observable<Response> postSync(String url,Map<String, String> params) {
        return post(url,params).map(new Function<Call, Response>() {
            @Override
            public Response apply(Call call) throws Exception {
                return call.execute();
            }
        });
    }

    Executor getDeliver() {
        if (mDeliver == null) {
            mDeliver = new Executor() {
                private final Handler mHandler = new Handler(Looper.getMainLooper());

                @Override
                public void execute(@NonNull Runnable command) {
                    mHandler.post(command);
                }
            };
        }
        return mDeliver;
    }

    public static abstract class OnResponseListener<M> implements Callback {

        @Override
        public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
            getInstance().getDeliver().execute(new Runnable() {
                @Override
                public void run() {
                    onFailed(call, e);
                }
            });
        }

        @Override
        public void onResponse(@NonNull final Call call, @NonNull Response response) throws IOException {
            final String json = response.body().string();
            //获取泛型类型
            Class clz = this.getClass();
            ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();
            try {
                //使用Gson进行解析
                final M data = GsonUtils.fromJson(json, types[0]);
                getInstance().getDeliver().execute(new Runnable() {
                    @Override
                    public void run() {
                        onSucceed(json, data);
                    }
                });
            } catch (final Exception e) {
                getInstance().getDeliver().execute(new Runnable() {
                    @Override
                    public void run() {
                        onFailed(call, e);
                    }
                });
            }
        }

        /**
         * 请求成功
         *
         * @param json
         * @param data
         */
        public abstract void onSucceed(String json, M data);

        /**
         * 请求失败
         *
         * @param call
         * @param e
         */
        public abstract void onFailed(Call call, Exception e);
    }
}
