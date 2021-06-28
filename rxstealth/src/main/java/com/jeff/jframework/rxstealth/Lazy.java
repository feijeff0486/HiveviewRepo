package com.jeff.jframework.rxstealth;

/**
 * 懒加载
 * <p>
 * @author Jeff
 * @date 2020/4/26 17:48
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
@FunctionalInterface
public interface Lazy<V> {
    V get();
}
