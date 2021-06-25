package com.jeff.jframework.tools.binder;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.jeff.jframework.core.CannotCreateException;
import com.jeff.jframework.tools.StringUtils;

/**
 * Binder机制辅助类
 *
 * 使用Intent传递大数据时可能会触发{@link android.os.TransactionTooLargeException}
 *
 * 1.此异常发生在Binder IPC调用过程中.
 * 2.客户端发送请求数据过大 服务端返回数据过大 都会触发 此异常。
 * 3.binder调用的缓冲buffer大小当前为1M ,由当前进程共享。因此，如果同时有多个调用，就算单个调用过程传输的数据不大，
 * 也有可能触发此异常。
 * 4.可以将数据打碎分片成小数据来避免此异常。
 *
 * <a href="https://blog.csdn.net/ylyg050518/article/details/97671874">原理</a>
 * <p>
 *
 * @author Jeff
 * @date 2020/9/11
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class BinderHelper {
    private BinderHelper(){
        throw new CannotCreateException(this.getClass());
    }

    /**
     * 借助Binder机制传递大数据
     * 将Bitmap通过跨进程的方式传过去了，避免了写磁盘
     * @param bundle
     * @param key
     * @param bitmap
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Bundle putBitmapBinder(@Nullable Bundle bundle,@NonNull String key,@NonNull Bitmap bitmap) {
        if (bitmap == null) return null;
        if (bundle == null)
            bundle = new Bundle();
        bundle.putBinder(StringUtils.isEmpty(key)?"bitmap":key,new BitmapBinder(bitmap));
        return bundle;
    }

    /**
     * 借助Binder机制传递大数据
     * 将Bitmap通过跨进程的方式传过去了，避免了写磁盘
     * @param bundle
     * @param key
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Bitmap getBitmapFromBinder(@NonNull Bundle bundle,@NonNull String key){
        if (bundle==null)return null;
        BitmapBinder bitmapBinder= (BitmapBinder) bundle.getBinder(StringUtils.isEmpty(key)?"bitmap":key);
        if (bitmapBinder==null){
            return null;
        }
        return bitmapBinder.getData();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static <T> Bundle putLargeData2Binder(@Nullable Bundle bundle, @NonNull String key, @NonNull T data) {
        if (data == null) return null;
        if (bundle == null)
            bundle = new Bundle();
        bundle.putBinder(StringUtils.isEmpty(key)?"default":key,new DockerBinder<T>(data));
        return bundle;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static <T> T getDataFromBinder(@NonNull Bundle bundle,@NonNull String key){
        if (bundle==null)return null;
        DockerBinder<T> dockerBinder= (DockerBinder<T>) bundle.getBinder(StringUtils.isEmpty(key)?"default":key);
        if (dockerBinder==null){
            return null;
        }
        return dockerBinder.getData();
    }

    public static class DockerBinder<T> extends Binder {
        private T data;

        public DockerBinder(T data) {
            this.data = data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class BitmapBinder extends DockerBinder<Bitmap> {

        public BitmapBinder(Bitmap data) {
            super(data);
        }
    }
}
