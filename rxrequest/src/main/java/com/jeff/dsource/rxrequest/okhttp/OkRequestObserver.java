package com.jeff.dsource.rxrequest.okhttp;

import com.jeff.dsource.rxrequest.AbstractObserver;
import com.jeff.jframework.tools.GsonUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 *
 * @author Jeff
 * @date 2020/6/15
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class OkRequestObserver<T> extends AbstractObserver<Call> {

    /**
     * 是否异步执行
     */
    private boolean async = true;

    public OkRequestObserver() {
        this(true);
    }

    public OkRequestObserver(boolean async) {
        super("OkRequest-Observer");
        this.async=async;
    }

    public abstract void onResult(T result);

    @Override
    public void onNext(Call call) {
        if (async){
            //加入队列，异步执行
            call.enqueue(new OkRequest.OnResponseListener<T>() {
                @Override
                public void onSucceed(String json, T data) {
                    onResult(data);
                }

                @Override
                public void onFailed(Call call, Exception e) {
                    onError(e);
                }
            });
        }else {
            //立即调用请求，并阻塞直到可以处理响应或错误为止
            try {
                Response response = call.execute();

                final String json = response.body().string();
                //获取泛型类型
                Class clz = this.getClass();
                ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
                Type[] types = type.getActualTypeArguments();
                try {
                    //使用Gson进行解析
                    final T data = GsonUtils.fromJson(json, types[0]);
                    OkRequest.getInstance().getDeliver().execute(new Runnable() {
                        @Override
                        public void run() {
                            onResult(data);
                        }
                    });
                } catch (final Exception e) {
                    OkRequest.getInstance().getDeliver().execute(new Runnable() {
                        @Override
                        public void run() {
                            onError(e);
                        }
                    });
                }
            } catch (final IOException e) {
                OkRequest.getInstance().getDeliver().execute(new Runnable() {
                    @Override
                    public void run() {
                        onError(e);
                    }
                });
            }
        }
    }

}
