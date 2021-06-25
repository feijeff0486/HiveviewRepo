package com.jeff.jframework.tools;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.jeff.jframework.core.CannotCreateException;
import com.jeff.jframework.core.ContextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;

/**
 * Wifi相关工具类
 * <p>
 *
 * @author Jeff
 * @date 2020/9/28
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class WifiUtils {
    private static final String TAG = "WifiUtils";
    public static final int WIFI_MAX_LEVEL = 5;

    @IntDef({WifiType.TYPE_NO_PASSWORD, WifiType.TYPE_WPA, WifiType.TYPE_WEP, WifiType.TYPE_WPA2})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WifiType {
        /**
         * 没有密码
         */
        int TYPE_NO_PASSWORD = WifiConfiguration.KeyMgmt.NONE;
        /**
         * wpa加密
         */
        int TYPE_WPA = WifiConfiguration.KeyMgmt.WPA_PSK;
        /**
         * 用wep加密
         */
        int TYPE_WEP = WifiConfiguration.KeyMgmt.WPA_EAP;
        /**
         * wpa2加密
         */
        int TYPE_WPA2 = 4;
    }

    private WifiUtils() {
        throw new CannotCreateException(this.getClass());
    }

    /**
     * Return whether wifi is enabled.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static boolean getWifiEnabled() {
        WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager == null) return false;
        return manager.isWifiEnabled();
    }

    /**
     * Set wifi enabled.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    public static void setWifiEnabled(final boolean enabled) {
        WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager == null) return;
        if (enabled == manager.isWifiEnabled()) return;
        manager.setWifiEnabled(enabled);
    }

    /**
     * 获取WIFI的当前强度
     *
     * @return
     * @Title getWifiLevel
     * @author haozening
     * @Description
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static int getWifiLevel() {
        WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null == manager || null == manager.getConnectionInfo()) {
            return 0;
        } else {
            return WifiManager.calculateSignalLevel(manager.getConnectionInfo().getRssi(), WIFI_MAX_LEVEL);
        }
    }

    /**
     * 获取本地ip地址
     *
     * @return
     */
    public static String getLocalIpAddressByWifi() {
        WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null == manager || null == manager.getConnectionInfo()) {
            return "";
        } else {
            return convertIp(manager.getConnectionInfo().getIpAddress());
        }
    }

    /**
     * 获取当前连接WiFi的SSID
     *
     * @return
     */
    public static String getConnectedSSID() {
        WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null == manager || null == manager.getConnectionInfo()) {
            return "";
        } else {
            return manager.getConnectionInfo().getSSID().replaceAll("\"", "");
        }
    }

    /**
     * 是否已连接指定wifi
     *
     * @param ssid
     */
    public static boolean isConnected2SSID(String ssid) {
        WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null == manager || null == manager.getConnectionInfo()) {
            return false;
        }
        switch (manager.getConnectionInfo().getSupplicantState()) {
            case AUTHENTICATING:
            case ASSOCIATING:
            case ASSOCIATED:
            case FOUR_WAY_HANDSHAKE:
            case GROUP_HANDSHAKE:
            case COMPLETED:
                return manager.getConnectionInfo().getSSID().replace("\"", "").equals(ssid);
            default:
                return false;
        }
    }

    /**
     * 是否连接过指定Wifi
     *
     * @param ssid
     */
    private static WifiConfiguration everConnected2SSID(String ssid) {
        WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null == manager) {
            return null;
        }
        List<WifiConfiguration> existingConfigs = manager.getConfiguredNetworks();
        if (existingConfigs == null || existingConfigs.isEmpty()) {
            return null;
        }
        ssid = "\"" + ssid + "\"";
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals(ssid)) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 构造wifi信息
     *
     * @param ssid
     * @param password
     * @param passType
     * @return
     */
    public static WifiConfiguration createWifiInfo(String ssid, String password, @WifiType int passType, boolean isClient) {
        Log.v(TAG, String.format("#createWifiInfo: [ssid= %s, password=%s, passType=%s]", ssid, password, passType));
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password length shorter than 8.");
        }
        if (isClient) {
            //如果曾经连接过，则移除
            WifiConfiguration tempConfig = everConnected2SSID(ssid);
            if (tempConfig != null) {
                WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (null != manager) {
                    boolean remove = manager.removeNetwork(tempConfig.networkId);
                    Log.i(TAG, "#createWifiInfo: removeNetwork result= " + remove);
                }
            }

            //作为客户端, 连接服务端wifi热点时要加双引号
            wifiConfiguration.SSID = "\"" + ssid + "\"";
            switch (passType) {
                case WifiType.TYPE_NO_PASSWORD:
                    wifiConfiguration.wepKeys[0] = "\"" + "\"";
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfiguration.wepTxKeyIndex = 0;
                    break;
                case WifiType.TYPE_WPA:
                case WifiType.TYPE_WPA2:
                    wifiConfiguration.preSharedKey = "\"" + password + "\"";
                    wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
                    wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);

                    wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    // 必须添加，否则无线路由无法连接
                    wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    break;
                default:
                    break;
            }
        } else {
            //作为服务端, 开放wifi热点时不需要加双引号
            wifiConfiguration.SSID = ssid;
            switch (passType) {
                case WifiType.TYPE_NO_PASSWORD:
                    wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    wifiConfiguration.allowedKeyManagement.set(WifiType.TYPE_NO_PASSWORD);
                    break;
                case WifiType.TYPE_WPA:
                case WifiType.TYPE_WPA2:
                    wifiConfiguration.preSharedKey = password;
                    wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wifiConfiguration.allowedKeyManagement.set(passType);
                    break;
                default:
                    break;
            }
        }
        return wifiConfiguration;
    }

    /**
     * 清除所有WiFi配置
     */
    public static void clearWifiConfig() {
        try {
            WifiManager manager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (null == manager) {
                return;
            }
            List<WifiConfiguration> list = manager.getConfiguredNetworks();
            if (list != null && list.size() > 0) {
                for (WifiConfiguration configuration : list) {
                    boolean result = manager.removeNetwork(configuration.networkId);
                    manager.saveConfiguration();
                    Log.i(TAG, "#clearWifiConfig: " + configuration.SSID + " = " + result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     * @see #getLocalIpAddressByWifi()
     * @deprecated
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi() {
        WifiManager wm = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    /**
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getGatewayByWifi() {
        WifiManager wm = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getNetMaskByWifi() {
        WifiManager wm = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getServerAddressByWifi() {
        WifiManager wm = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }

    /**
     * 获取路由器的mac地址
     *
     * @return
     */
    public static String getRouterWifiMacAddress() {
        String str = "";
        WifiManager wifiManager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            try {
                List scanResults = wifiManager.getScanResults();
                WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (!(scanResults == null || connectionInfo == null)) {
                    int i = 0;
                    while (i < scanResults.size()) {
                        ScanResult scanResult = (ScanResult) scanResults.get(i);
                        i++;
                        str = TextUtils.equals(connectionInfo.getBSSID(), scanResult.BSSID) ? scanResult.BSSID : str;
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    private static String convertIp(int ipAddress) {
        if (ipAddress == 0) return "";
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }
}
