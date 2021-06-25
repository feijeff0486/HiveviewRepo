package com.jeff.jframework.core;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Context提供者，首先调用{@link ContextUtils#init(Context)}进行初始化
 *
 * @author Jeff
 * @date 2020/07/29 19:19
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class ContextUtils {
    private static final String TAG = "ContextUtils";
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private ContextUtils() {
        throw new CannotCreateException(getClass());
    }

    /**
     * 初始化工具类，尽早初始化这里
     * 可能存在的问题：当项目中存在ContentProvider时，{@link ContentProvider#onCreate()}的调用
     * 比{@link Application#onCreate()}要早；在{@link ContentProvider#onCreate()}调用
     * {@link #getContext()}无法获取context对象；
     *
     * @param context 上下文
     */
    public static synchronized void init(@NonNull final Context context) {
        if (ContextUtils.sContext!=null){
            return;
        }
        Log.d(TAG, "init");
        ContextUtils.sContext = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        Preconditions.checkNotNull(sContext,"ContextUtils should be initialized in application");
        return sContext;
    }

    public static String getString(@StringRes int resId){
        return getContext().getString(resId);
    }

    public static int getColor(int resId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getContext().getColor(resId);
        }else {
            return getResources().getColor(resId);
        }
    }

    public static float getDimen(@DimenRes int id){
        return getResources().getDimension(id);
    }

    public static Drawable getDrawable(int resId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getContext().getDrawable(resId);
        }else {
            return getResources().getDrawable(resId);
        }
    }

    public static Resources getResources(){
        return getContext().getResources();
    }

    public static File getCacheDir(){
        return getContext().getCacheDir();
    }

    public static AssetManager getAssets(){
        return getContext().getAssets();
    }

    public static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            Preconditions.checkNotNull(app,"You should init first");
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("You should init first");
    }
}
