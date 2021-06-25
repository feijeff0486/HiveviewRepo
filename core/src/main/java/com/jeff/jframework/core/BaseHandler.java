package com.jeff.jframework.core;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * 弱引用Handler基类
 * @author Jeff
 * @date 2020/07/31 16:59
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class BaseHandler<T> extends Handler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    public static final int WHAT_TARGET_GONE = -1;
    private static final int WORK_IN_UNKNOWN = 0x000;
    private static final int WORK_IN_ACTIVITY = 0x001;
    private static final int WORK_IN_FRAGMENT = 0x002;
    private int mWorkFlag = WORK_IN_UNKNOWN;
    private WeakReference<T> mHostRef;

    /**
     * 构造私有化,让调用者必须传递一个Activity 或者 Fragment
     */
    private BaseHandler() {}

    public BaseHandler(T host) {
        mHostRef = new WeakReference<>(host);
        if (host instanceof Activity) {
            mWorkFlag = WORK_IN_ACTIVITY;
        } else if (host instanceof Fragment) {
            mWorkFlag = WORK_IN_FRAGMENT;
        } else {
            mWorkFlag = WORK_IN_UNKNOWN;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (getHost() == null) {
            Log.e(TAG, "Host is gone!");
            handleException(WHAT_TARGET_GONE);
            return;
        }
        switch (mWorkFlag) {
            case WORK_IN_ACTIVITY:
                if (((Activity)getHost()).isFinishing()){
                    Log.e(TAG, "Activity is finishing!");
                    handleException(WHAT_TARGET_GONE);
                }else {
                    handleMessage(msg, msg.what);
                }
                break;
            case WORK_IN_FRAGMENT:
                if (((Fragment)getHost()).isRemoving()){
                    Log.e(TAG, "Fragment is removing!");
                    handleException(WHAT_TARGET_GONE);
                }else {
                    handleMessage(msg, msg.what);
                }
                break;
            case WORK_IN_UNKNOWN:
                handleMessage(msg, msg.what);
                break;
            default:
                break;
        }
    }

    /**
     * 子类override处理事件
     * @param msg
     * @param what
     */
    protected abstract void handleMessage(Message msg, int what);

    /**
     * 处理异常
     * @param errorCode
     */
    protected void handleException(int errorCode) {
        Log.e(TAG, "handleException: errorCode= " + errorCode);
    }

    protected T getHost() {
        if (mHostRef != null && mHostRef.get() != null) {
            return mHostRef.get();
        }
        return null;
    }

}
