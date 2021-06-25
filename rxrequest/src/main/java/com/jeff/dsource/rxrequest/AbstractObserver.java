package com.jeff.dsource.rxrequest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.jeff.dsource.rxrequest.exception.ExceptionHandler;
import com.jeff.dsource.rxrequest.exception.ResponseException;
import com.jeff.jframework.tools.NetworkUtils;

import io.reactivex.observers.DisposableObserver;

/**
 * 订阅请求
 *
 * @author afei
 * @date 2021/04/08 11:15
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class AbstractObserver<T> extends DisposableObserver<T> {
    protected final String TAG;

    public AbstractObserver(@NonNull String tag) {
        TAG = "RxRequest-" + tag;
    }

    public void onNetworkErrorBeforeRequest(){
        //no-op
    }

    public abstract void onError(int code, String message);

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: http is start >>>>>>>>>>>>>>");
        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetworkUtils.isConnected()) {
            Log.i(TAG, "onStart: 无网络...");
            onNetworkErrorBeforeRequest();
            onComplete();
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        ResponseException responseException = ExceptionHandler.handleException(e);
        Log.e(TAG, String.format("error: %d, message: %s",responseException.code,responseException.getMessage()));
        onError(responseException.code,responseException.getMessage());
        //其他全部甩锅网络异常
//        ToastUtils.shortToast("网络异常");
    }

    @Override
    public void onComplete() {
        // TODO: 4/12/21 You must call emitter.onComplete() if u want the method invoke!
        Log.i(TAG, "onComplete: <<<<<<<<<<<<<<");
    }
}
