package com.jeff.jframework.tools.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.jeff.jframework.core.ContextUtils;
import com.jeff.jframework.core.UIHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SharedPreference工具类
 *
 * @author Jeff
 * @describe
 * @date 2018/3/2.
 */
public final class PreferencesUtils implements IPreference {
    private static Map<String, IPreference> preferenceMap = new ConcurrentHashMap<>();
    private SharedPreferences preferences;

    public static IPreference getPreference() {
        return getPreference("shared_preferences");
    }

    public static IPreference getPreference(String fileName) {
        IPreference sp = preferenceMap.get(fileName);
        if (sp == null) {
            sp = new PreferencesUtils(fileName);
            preferenceMap.put(fileName, sp);
        }
        return sp;
    }

    /**
     * Instantiates a new Pref manager.
     *
     * @param fileName the file name
     */
    private PreferencesUtils(String fileName) {
        preferences = ContextUtils.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    @Override
    public void put(String key, Object value) {
        SharedPreferences.Editor edit = preferences.edit();
        put(edit, key, value);
        edit.apply();
    }

    private void smartCommit(SharedPreferences.Editor editor){
        // commit提交是同步过程，效率会比apply异步提交的速度慢，但是apply没有返回值，永远无法知道存储是否失败。
        // 在不关心提交结果是否成功的情况下，优先考虑apply方法
        if (UIHandler.isMainThread()){
            // async 原子性 apply方法的原子操作是原子提交的内存中，而非数据库，
            // 所以在提交到内存中时不可打断，之后再异步提交数据到数据库中，因此也不会有相应的返回值
            editor.apply();
        }else {
            // sync 原子性 commit是原子提交到数据库，所以从提交数据到存在Disk中都是同步过程，中间不可打断
            editor.commit();
        }
    }

    /**
     * 保存一个Map集合
     *
     * @param map
     */
    @Override
    public <T> void putAll(Map<String, T> map) {
        SharedPreferences.Editor edit = preferences.edit();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            put(edit, key, value);
        }
        edit.apply();
    }

    @Override
    public void putAll(String key, List<String> list) {
        putAll(key, list, new ComparatorImpl());
    }

    @Override
    public void putAll(String key, List<String> list, Comparator<String> comparator) {
        Set<String> set = new TreeSet<>(comparator);
        set.addAll(list);
        preferences.edit().putStringSet(key, set).apply();
    }

    /**
     * 根据key取出一个数据
     *
     * @param key 键
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, @DataType int type) {
        return (T) getValue(key, type);
    }

    @Override
    public <T> T get(String key, @DataType int type, T defaultValue) {
        return (T) getValue(key, type, defaultValue);
    }

    @Override
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    @Override
    public List<String> getAll(String key) {
        Set<String> set = get(key, DataType.STRING_SET);
        return new ArrayList<>(set);
    }

    @Override
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    @Override
    public void removeAll(List<String> keys) {
        SharedPreferences.Editor edit = preferences.edit();
        for (String k : keys) {
            edit.remove(k);
        }
        edit.apply();
    }

    @Override
    public void removeAll(String[] keys) {
        removeAll(Arrays.asList(keys));
    }

    @Override
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    @Override
    public void clear() {
        preferences.edit().clear().apply();
    }

    @Override
    public String getString(String key) {
        return get(key, DataType.STRING);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return get(key, DataType.STRING, defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return get(key, DataType.FLOAT);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return get(key, DataType.FLOAT, defaultValue);
    }

    @Override
    public int getInteger(String key) {
        return get(key, DataType.INTEGER);
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        return get(key, DataType.INTEGER, defaultValue);
    }

    @Override
    public long getLong(String key) {
        return get(key, DataType.LONG);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return get(key, DataType.LONG, defaultValue);
    }

    @Override
    public Set<String> getSet(String key) {
        return get(key, DataType.STRING_SET);
    }

    @Override
    public Set<String> getSet(String key, Set<String> defaultValue) {
        return get(key, DataType.STRING_SET, defaultValue);
    }

    @Override
    public boolean getBoolean(String key) {
        return get(key, DataType.BOOLEAN);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return get(key, DataType.BOOLEAN, defaultValue);
    }

    /**
     * 保存数据
     *
     * @param editor
     * @param key
     * @param obj
     */
    @SuppressWarnings("unchecked")
    private void put(SharedPreferences.Editor editor, String key, Object obj) {
        // key 不为null时再存入，否则不存储
        if (key != null) {
            if (obj instanceof Integer) {
                editor.putInt(key, (Integer) obj);
            } else if (obj instanceof Long) {
                editor.putLong(key, (Long) obj);
            } else if (obj instanceof Boolean) {
                editor.putBoolean(key, (Boolean) obj);
            } else if (obj instanceof Float) {
                editor.putFloat(key, (Float) obj);
            } else if (obj instanceof Set) {
                editor.putStringSet(key, (Set<String>) obj);
            } else if (obj instanceof String) {
                editor.putString(key, String.valueOf(obj));
            }
        }
    }

    private Object getValue(String key, @DataType int type, Object defaultValue) {
        Object value = getValue(key, type);
        return value != null ? value : defaultValue;
    }

    /**
     * 根据key和类型取出数据
     *
     * @param key
     * @return
     */
    private Object getValue(String key, @DataType int type) {
        switch (type) {
            case DataType.INTEGER:
                return preferences.getInt(key, -1);
            case DataType.FLOAT:
                return preferences.getFloat(key, -1f);
            case DataType.BOOLEAN:
                return preferences.getBoolean(key, false);
            case DataType.LONG:
                return preferences.getLong(key, -1L);
            case DataType.STRING:
                return preferences.getString(key, null);
            case DataType.STRING_SET:
                return preferences.getStringSet(key, null);
            default: // 默认取出String类型的数据
                return null;
        }
    }
}
