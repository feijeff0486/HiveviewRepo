package com.jeff.jframework.tools;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/7/2
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class NetworkManager {
    private static final String TAG = "NetworkManager";
    /**
     * 接收wifi ap 状态变化的广播action
     */
    public static final String ACTION_WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    private static volatile NetworkManager sInstance;
    private static boolean enableLog = true;
    private Context mContext;
    private Handler mMainHandler;
    private final LinkedList<OnNetworkChangedListener> networkChangedListeners;
    private volatile NetworkInfo mNetworkInfo;
    private NetworkChangedReceiver networkChangedReceiver;
    private volatile @NetworkUtils.NetWorkType
    int mNetType = NetworkUtils.NetWorkType.NETWORK_NO;

    private final Object lock = new Object();

    @SuppressLint("MissingPermission")
    private NetworkManager(Context context) {
        this.mContext = context;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.networkChangedListeners = new LinkedList<>();
        updateNetType(NetworkUtils.getActiveNetworkInfo());
    }

    public static NetworkManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NetworkManager.class) {
                if (sInstance == null) {
                    sInstance = new NetworkManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public void logging(boolean logging) {
        enableLog = logging;
    }

    @SuppressLint("MissingPermission")
    private void updateNetType(NetworkInfo info) {
        this.mNetworkInfo = info;
        mNetType = NetworkUtils.getNetworkType(info);
    }

    public void registerNetworkChangedReceiver() {
        try {
            if (mContext == null) return;
            if (this.networkChangedReceiver == null) {
                this.networkChangedReceiver = new NetworkChangedReceiver();
                this.mContext.registerReceiver(this.networkChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterNetworkChangedReceiver() {
        try {
            if (this.mContext == null || this.networkChangedReceiver == null) {
                return;
            }
            this.mContext.unregisterReceiver(this.networkChangedReceiver);
            networkChangedListeners.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加网络状态变化监听，在这之前需要先调用{@link #registerNetworkChangedReceiver()}
     *
     * @param listener
     */
    public void addNetworkChangedListener(OnNetworkChangedListener listener) {
        if (listener == null) return;
        synchronized (networkChangedListeners) {
            if (!networkChangedListeners.contains(listener)) {
                this.networkChangedListeners.add(listener);
            }
        }
    }

    /**
     * 移除网络状态变化监听
     *
     * @param listener
     */
    public void removeNetworkChangedListener(OnNetworkChangedListener listener) {
        if (listener == null) return;
        synchronized (networkChangedListeners) {
            this.networkChangedListeners.remove(listener);
        }
    }

    @SuppressLint("MissingPermission")
    private void notifyNetwork(Intent intent) {
        if (intent != null) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo oldInfo;
                NetworkInfo newInfo;
                synchronized (this.lock) {
                    oldInfo = this.mNetworkInfo;
                    newInfo = NetworkUtils.getActiveNetworkInfo();
                    updateNetType(newInfo);
                }
                if (isConnected(oldInfo)) {
                    if (!isConnected(newInfo)) {
                        notifyNetworkDisconnected(NetworkUtils.getNetworkType(oldInfo));
                    } else if (oldInfo.getType() != newInfo.getType()) {
                        notifyNetworkSwitched(NetworkUtils.getNetworkType(oldInfo), NetworkUtils.getNetworkType(newInfo));
                    }
                } else if (isConnected(newInfo)) {
                    notifyNetworkConnected(NetworkUtils.getNetworkType(newInfo));
                }
            }
        }
    }

    private void notifyNetworkSwitched(final @NetworkUtils.NetWorkType int oldType, final @NetworkUtils.NetWorkType int newType) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (NetworkManager.this.networkChangedListeners) {
                    if (NetworkManager.enableLog) {
                        Log.d(TAG, "notifyNetworkSwitched oldNetworkType=" + NetworkUtils.getNetworkType(oldType) + " newNetworkType=" + NetworkUtils.getNetworkType(newType));
                    }
                    Iterator it = NetworkManager.this.networkChangedListeners.iterator();
                    while (it.hasNext()) {
                        ((OnNetworkChangedListener) it.next()).onNetworkChanged(oldType, newType);
                    }
                }
            }
        });
    }

    private void notifyNetworkConnected(final @NetworkUtils.NetWorkType int type) {
        this.mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (NetworkManager.this.networkChangedListeners) {
                    if (NetworkManager.enableLog) {
                        Log.d(TAG, "notifyNetworkConnected networkType=" + NetworkUtils.getNetworkType(type));
                    }
                    Iterator it = NetworkManager.this.networkChangedListeners.iterator();
                    while (it.hasNext()) {
                        ((OnNetworkChangedListener) it.next()).onConnected(type);
                    }
                }
            }
        });
    }

    private void notifyNetworkDisconnected(final @NetworkUtils.NetWorkType int type) {
        this.mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (NetworkManager.this.networkChangedListeners) {
                    if (NetworkManager.enableLog) {
                        Log.d(TAG, "notifyNetworkDisconnected networkType=" + NetworkUtils.getNetworkType(type));
                    }
                    Iterator it = NetworkManager.this.networkChangedListeners.iterator();
                    while (it.hasNext()) {
                        ((OnNetworkChangedListener) it.next()).onDisconnect(type);
                    }
                }
            }
        });
    }

    public boolean isConnected() {
        boolean connect = false;
        synchronized (this.lock) {
            connect = this.mNetworkInfo != null && this.mNetworkInfo.isConnected();
        }
        return connect;
    }

    private boolean isConnected(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected();
    }

    public void release() {
        unregisterNetworkChangedReceiver();
        mContext = null;
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        private NetworkChangedReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkManager.this.notifyNetwork(intent);
        }
    }

}
