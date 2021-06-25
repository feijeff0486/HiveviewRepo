package com.jeff.dsource.rxrequest.cache;

import android.util.Log;

import com.jeff.jframework.tools.file.FileUtils;

import java.io.File;

import okhttp3.Cache;

/**
 * okhttp缓存管理
 * <p>
 *
 * @author Jeff
 * @date 2020/6/12
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class CacheManager {
    /**
     * 缓存大小10MB
     */
    private static final int CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static final String CACHE_DIR="okhttp";
    private static volatile CacheManager sInstance;
    private Cache cache;

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        if (sInstance == null) {
            synchronized (CacheManager.class) {
                if (sInstance == null) {
                    sInstance = new CacheManager();
                }
            }
        }
        return sInstance;
    }

    private void cacheConfig() {
        try {
            //缓存到cache路径
            File cacheDir = new File(FileUtils.getCacheDir(), CACHE_DIR);
            cache = new Cache(cacheDir, CACHE_MAX_SIZE);
        } catch (Exception e) {
            Log.e("Cache", "Could not create http cache", e);
        }
    }

    public Cache getCache() {
        if (cache==null){
            cacheConfig();
        }
        return cache;
    }
}
