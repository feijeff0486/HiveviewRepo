package com.jeff.jframework.tools;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.widget.Toast;

import com.jeff.jframework.core.CannotCreateException;
import com.jeff.jframework.core.ContextUtils;

/**
 * Toast工具类
 * <p>
 * @author Jeff
 * @date 2018/12/06 09:43
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class ToastUtils {
    private static Toast mToast;

    private ToastUtils(){
        throw new CannotCreateException(getClass());
    }

    @UiThread
    public static void showToast(String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(ContextUtils.getContext(), msg, duration);
        } else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    @UiThread
    public static void shortToast(@NonNull String msg){
        showToast(msg, Toast.LENGTH_SHORT);
    }

    @UiThread
    public static void shortToast(@StringRes int resId){
        String msg=ContextUtils.getString(resId);
        shortToast(msg);
    }

    @UiThread
    public static void longToast(@NonNull String msg){
        showToast(msg, Toast.LENGTH_LONG);
    }

    @UiThread
    public static void longToast(@StringRes int resId){
        String msg=ContextUtils.getString(resId);
        longToast(msg);
    }

}
