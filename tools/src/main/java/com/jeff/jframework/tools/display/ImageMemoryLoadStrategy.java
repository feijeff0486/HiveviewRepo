package com.jeff.jframework.tools.display;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.util.Log;

/**
 * 通过内存状态管理图片加载
 *
 * @author Jeff
 * @date 2020/8/7
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 * @see ComponentCallbacks2
 * 1.是一个细粒度的内存回收管理回调。
 * 2.Application、Activity、Service、ContentProvider、Fragment实现了ComponentCallback2接口
 * 3.开发者应该实现onTrimMemory(int)方法，细粒度release 内存，参数可以体现不同程度的内存可用情况
 * 4.响应onTrimMemory回调：开发者的app会直接受益，有利于用户体验，系统更有可能让app存活的更持久。
 * 5.不响应onTrimMemory回调：系统更有可能kill 进程
 * <p>
 */
public class ImageMemoryLoadStrategy implements ComponentCallbacks2 {
    private static final String TAG = "ImageMemoryLoadStrategy";
    /**
     * 内存初始状态的值
     */
    private static final int TRIM_MEMORY_INITIAL = -1;
    private static int sLastMemoryState = TRIM_MEMORY_INITIAL;

    /**
     * 设备内存状态回调
     * @param level
     *
     * 内存变换
     * @see #TRIM_MEMORY_RUNNING_CRITICAL 设备运行特别慢，当前app还不会被杀死，但是如果此app没有释放资源，系统将会kill后台进程
     * @see #TRIM_MEMORY_MODERATE 设备开始运行缓慢，当前app正在运行，不会被kil
     * @see #TRIM_MEMORY_RUNNING_LOW 设备运行更缓慢了，当前app正在运行，不会被kill。但是请回收unused资源，以便提升系统的性能。
     *
     * app可见状态变化
     * @see #TRIM_MEMORY_UI_HIDDEN 当前app UI不再可见，这是一个回收大个资源的好时机
     *
     * app进程被置于background LRU list
     * @see #TRIM_MEMORY_BACKGROUND 系统运行慢，并且进程位于LRU list的上端。尽管app不处于高风险被kill。当前app应该释放那些容易恢复的资源
     * @see #TRIM_MEMORY_MODERATE 系统运行缓慢，当前进程已经位于LRU list的中部，如果系统进一步变慢，便会有被kill的可能
     * @see #TRIM_MEMORY_COMPLETE 系统运行慢，当前进程是第一批将被系统kill的进程。此app应该释放一切可以释放的资源。低于api 14的，用户可以使用onLowMemory回调。
     */
    @Override
    public void onTrimMemory(int level) {
        if (TRIM_MEMORY_RUNNING_CRITICAL == level
                || TRIM_MEMORY_MODERATE == level
                || TRIM_MEMORY_RUNNING_LOW == level) {
            Log.d(TAG, "onTrimMemory: " + level);
            sLastMemoryState = level;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //no-op
    }

    @Override
    public void onLowMemory() {
        sLastMemoryState = TRIM_MEMORY_RUNNING_LOW;
    }

    public static int getBitmapSize(int baseSize) {
        return (int) (baseSize * getBitmapMultiple());
    }

    /**
     * 根据当前内存状态获取图片裁剪比例
     *
     * @return
     */
    private static float getBitmapMultiple() {
        if (sLastMemoryState == TRIM_MEMORY_RUNNING_LOW)
            return 0.8f;
        else return 1f;
    }
}
