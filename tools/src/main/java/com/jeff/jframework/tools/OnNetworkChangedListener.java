package com.jeff.jframework.tools;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/7/18
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public interface OnNetworkChangedListener {
    void onConnected(int newType);

    void onNetworkChanged(int oldType, int newType);

    void onDisconnect(int oldType);
}
