package com.jeff.jframework.tools.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jeff
 * @describe
 * @date 2018/11/13.
 */
public class LruDoubleCache {
    private static final ConcurrentHashMap<String, LruDoubleCache> CACHE_MAP = new ConcurrentHashMap<>();
    private LruMemoryCache mLruMemoryCache;
    private LruDiskCache   mLruDiskCache;

    /**
     * Return the single {@link LruDoubleCache} instance.
     *
     * @return the single {@link LruDoubleCache} instance
     */
    public static LruDoubleCache getInstance() {
        return getInstance(LruMemoryCache.getInstance(), LruDiskCache.getInstance());
    }

    /**
     * Return the single {@link LruDoubleCache} instance.
     *
     * @param cacheMemoryUtils The instance of {@link LruMemoryCache}.
     * @param cacheDiskUtils   The instance of {@link LruDiskCache}.
     * @return the single {@link LruDoubleCache} instance
     */
    public static LruDoubleCache getInstance(@NonNull final LruMemoryCache cacheMemoryUtils,
                                             @NonNull final LruDiskCache cacheDiskUtils) {
        final String cacheKey = cacheDiskUtils.toString() + "_" + cacheMemoryUtils.toString();
        LruDoubleCache cache = CACHE_MAP.get(cacheKey);
        if (cache == null) {
            cache = new LruDoubleCache(cacheMemoryUtils, cacheDiskUtils);
            CACHE_MAP.put(cacheKey, cache);
        }
        return cache;
    }

    private LruDoubleCache(LruMemoryCache cacheMemoryUtils, LruDiskCache cacheUtils) {
        mLruMemoryCache = cacheMemoryUtils;
        mLruDiskCache = cacheUtils;
    }

    /**
     * Put value in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, Object value) {
        put(key, value,-1);
    }

    /**
     * Put value in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, Object value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about bytes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put bytes in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final byte[] value) {
        put(key, value, -1);
    }

    /**
     * Put bytes in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, byte[] value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the bytes in cache.
     *
     * @param key The key of cache.
     * @return the bytes if cache exists or null otherwise
     */
    public byte[] getBytes(@NonNull final String key) {
        return getBytes(key, null);
    }

    /**
     * Return the bytes in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bytes if cache exists or defaultValue otherwise
     */
    public byte[] getBytes(@NonNull final String key, final byte[] defaultValue) {
        byte[] obj = mLruMemoryCache.get(key);
        if (obj != null) return obj;
        return mLruDiskCache.getBytes(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put string value in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final String value) {
        put(key, value, -1);
    }

    /**
     * Put string value in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final String value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the string value in cache.
     *
     * @param key The key of cache.
     * @return the string value if cache exists or null otherwise
     */
    public String getString(@NonNull final String key) {
        return getString(key, null);
    }

    /**
     * Return the string value in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the string value if cache exists or defaultValue otherwise
     */
    public String getString(@NonNull final String key, final String defaultValue) {
        String obj = mLruMemoryCache.get(key);
        if (obj != null) return obj;
        return mLruDiskCache.getString(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put JSONObject in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final JSONObject value) {
        put(key, value, -1);
    }

    /**
     * Put JSONObject in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key,
                    final JSONObject value,
                    final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the JSONObject in cache.
     *
     * @param key The key of cache.
     * @return the JSONObject if cache exists or null otherwise
     */
    public JSONObject getJSONObject(@NonNull final String key) {
        return getJSONObject(key, null);
    }

    /**
     * Return the JSONObject in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    public JSONObject getJSONObject(@NonNull final String key, final JSONObject defaultValue) {
        JSONObject obj = mLruMemoryCache.get(key);
        if (obj != null) return obj;
        return mLruDiskCache.getJSONObject(key, defaultValue);
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put JSONArray in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final JSONArray value) {
        put(key, value, -1);
    }

    /**
     * Put JSONArray in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final JSONArray value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the JSONArray in cache.
     *
     * @param key The key of cache.
     * @return the JSONArray if cache exists or null otherwise
     */
    public JSONArray getJSONArray(@NonNull final String key) {
        return getJSONArray(key, null);
    }

    /**
     * Return the JSONArray in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    public JSONArray getJSONArray(@NonNull final String key, final JSONArray defaultValue) {
        JSONArray obj = mLruMemoryCache.get(key);
        if (obj != null) return obj;
        return mLruDiskCache.getJSONArray(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put bitmap in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Bitmap value) {
        put(key, value, -1);
    }

    /**
     * Put bitmap in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Bitmap value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the bitmap in cache.
     *
     * @param key The key of cache.
     * @return the bitmap if cache exists or null otherwise
     */
    public Bitmap getBitmap(@NonNull final String key) {
        return getBitmap(key, null);
    }

    /**
     * Return the bitmap in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    public Bitmap getBitmap(@NonNull final String key, final Bitmap defaultValue) {
        Bitmap obj = mLruMemoryCache.get(key);
        if (obj != null) return obj;
        return mLruDiskCache.getBitmap(key, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put drawable in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Drawable value) {
        put(key, value, -1);
    }

    /**
     * Put drawable in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Drawable value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the drawable in cache.
     *
     * @param key The key of cache.
     * @return the drawable if cache exists or null otherwise
     */
    public Drawable getDrawable(@NonNull final String key) {
        return getDrawable(key, null);
    }

    /**
     * Return the drawable in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the drawable if cache exists or defaultValue otherwise
     */
    public Drawable getDrawable(@NonNull final String key, final Drawable defaultValue) {
        Drawable obj = mLruMemoryCache.get(key);
        if (obj != null) return obj;
        return mLruDiskCache.getDrawable(key, defaultValue);
    }


    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put parcelable in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Parcelable value) {
        put(key, value, -1);
    }

    /**
     * Put parcelable in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Parcelable value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the parcelable in cache.
     *
     * @param key     The key of cache.
     * @param creator The creator.
     * @param <T>     The value type.
     * @return the parcelable if cache exists or null otherwise
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator) {
        return getParcelable(key, creator, null);
    }

    /**
     * Return the parcelable in cache.
     *
     * @param key          The key of cache.
     * @param creator      The creator.
     * @param defaultValue The default value if the cache doesn't exist.
     * @param <T>          The value type.
     * @return the parcelable if cache exists or defaultValue otherwise
     */
    public <T> T getParcelable(@NonNull final String key,
                               @NonNull final Parcelable.Creator<T> creator,
                               final T defaultValue) {
        T value = mLruMemoryCache.get(key);
        if (value != null) return value;
        return mLruDiskCache.getParcelable(key, creator, defaultValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Put serializable in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Serializable value) {
        put(key, value, -1);
    }

    /**
     * Put serializable in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Serializable value, final int saveTime) {
        mLruMemoryCache.put(key, value, saveTime);
        mLruDiskCache.put(key, value, saveTime);
    }

    /**
     * Return the serializable in cache.
     *
     * @param key The key of cache.
     * @return the bitmap if cache exists or null otherwise
     */
    public Object getSerializable(@NonNull final String key) {
        return getSerializable(key, null);
    }

    /**
     * Return the serializable in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    public Object getSerializable(@NonNull final String key, final Object defaultValue) {
        Object obj = mLruMemoryCache.get(key);
        if (obj != null) return obj;
        return mLruDiskCache.getSerializable(key, defaultValue);
    }

    /**
     * Return the size of cache in disk.
     *
     * @return the size of cache in disk
     */
    public long getCacheDiskSize() {
        return mLruDiskCache.getCacheSize();
    }

    /**
     * Return the count of cache in disk.
     *
     * @return the count of cache in disk
     */
    public int getCacheDiskCount() {
        return mLruDiskCache.getCacheCount();
    }

    /**
     * Return the count of cache in memory.
     *
     * @return the count of cache in memory.
     */
    public int getCacheMemoryCount() {
        return mLruMemoryCache.getCacheCount();
    }

    /**
     * Remove the cache by key.
     *
     * @param key The key of cache.
     */
    public void remove(@NonNull String key) {
        mLruMemoryCache.remove(key);
        mLruDiskCache.remove(key);
    }

    /**
     * Clear all of the cache.
     */
    public void clear() {
        mLruMemoryCache.clear();
        mLruDiskCache.clear();
    }
}
