package com.jeff.jframework.tools.preference;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * <p>
 * @author Jeff
 * @date 2020/04/17 10:34
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public interface IPreference {

    /**
     * 保存一个数据
     * @param key 键
     * @param value 值
     */
    <T> void put(String key, T value);

    /**
     * 保存一个Map集合
     * @param map
     */
    <T> void putAll(Map<String, T> map);

    /**
     * 保存一个List集合
     * @param key
     * @param list
     */
    void putAll(String key, List<String> list);

    /**
     * 保存一个List集合，并且自定保存顺序
     * @param key
     * @param list
     * @param comparator
     */
    void putAll(String key, List<String> list, Comparator<String> comparator);

    /**
     * 根据key取出一个数据
     * @param key 键
     */
    <T> T get(String key, @DataType int type);

    /**
     * 根据key取出一个数据
     * @param key 键
     * @param defaultValue
     */
    <T> T get(String key, @DataType int type, T defaultValue);

    /**
     * 取出全部数据
     */
    Map<String, ?> getAll();

    /**
     * 取出一个List集合
     * @param key
     * @return
     */
    List<String> getAll(String key);

    /**
     * 移除一个数据
     * @param key
     * @return
     */
    void remove(String key);

    /**
     * 移除一个集合的数据
     * @param keys
     * @return
     */
    void removeAll(List<String> keys);

    /**
     * 移除一个集合的数据
     * @param keys
     * @return
     */
    void removeAll(String[] keys);

    /**
     * 是否存在key
     * @return
     */
    boolean contains(String key);

    /**
     * 清除全部数据
     */
    void clear();

    /**
     * 获取String类型的数据
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * 获取String类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    String getString(String key, String defaultValue);

    /**
     * 获取Float类型的数据
     * @param key
     * @return
     */
    float getFloat(String key);

    /**
     * 获取Float类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    float getFloat(String key, float defaultValue);

    /**
     * 获取int类型的数据
     * @return
     */
    int getInteger(String key);

    /**
     * 获取int类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    int getInteger(String key, int defaultValue);

    /**
     * 获取long类型的数据
     * @param key
     * @return
     */
    long getLong(String key);

    /**
     * 获取long类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    long getLong(String key, long defaultValue);

    /**
     * 获取Set类型的数据
     * @param key
     * @return
     */
    Set<String> getSet(String key);

    /**
     * 获取Set类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    Set<String> getSet(String key, Set<String> defaultValue);

    /**
     * 获取boolean类型的数据
     * @param key
     * @return
     */
    boolean getBoolean(String key);

    /**
     * 获取boolean类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    boolean getBoolean(String key, boolean defaultValue);
}
