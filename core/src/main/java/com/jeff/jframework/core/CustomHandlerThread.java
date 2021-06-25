package com.jeff.jframework.core;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * 自定义HandlerThread的抽象类
 * @author Jeff
 * @date 2019/11/24 15:26
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class CustomHandlerThread {
    public static final int MSG_POST_RUNNABLE=100000;
    protected Handler mHandler;
    private HandlerThread mHandlerThread;
    private String name;

    protected abstract void processMessage(Message message);

    public CustomHandlerThread(String name) {
        this(name, 0);
    }

    public CustomHandlerThread(String name, int priority) {
        this.name = name;
        this.mHandlerThread = new HandlerThread(name, priority);
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_POST_RUNNABLE:
                        Runnable r = (Runnable) msg.obj;
                        if (r != null) {
                            r.run();
                            return;
                        }
                        return;
                    default:
                        CustomHandlerThread.this.processMessage(msg);
                        return;
                }
            }
        };
    }

    public Message obtainMessage() {
        return this.mHandler.obtainMessage();
    }

    public Message obtainMessage(int what) {
        return this.mHandler.obtainMessage(what);
    }

    public Message obtainMessage(int what, Object obj) {
        return this.mHandler.obtainMessage(what, obj);
    }

    public void sendMessage(Message msg) {
        this.mHandler.sendMessage(msg);
    }

    public void sendMessageDelayed(Message msg, long delayMillis) {
        this.mHandler.sendMessageDelayed(msg, delayMillis);
    }

    public void removeMessage(int what) {
        this.mHandler.removeMessages(what);
    }

    public void removeMessage(int what, Object obj) {
        this.mHandler.removeMessages(what, obj);
    }

    public final boolean post(Runnable r) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = MSG_POST_RUNNABLE;
        msg.obj = r;
        return this.mHandler.sendMessage(msg);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return this.mHandler.postDelayed(r, delayMillis);
    }

    public boolean destroy() {
        mHandler.removeCallbacksAndMessages(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return this.mHandlerThread.quitSafely();
        }else {
            return this.mHandlerThread.quit();
        }
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public HandlerThread getHandlerThread() {
        return this.mHandlerThread;
    }
}
