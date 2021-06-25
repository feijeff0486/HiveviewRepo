package com.jeff.dsource.rxrequest;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jeff.dsource.rxrequest.exception.ExceptionHandler;
import com.jeff.dsource.rxrequest.exception.ResponseException;
import com.jeff.dsource.rxrequest.exception.ResponseUnavailableException;
import com.jeff.dsource.rxrequest.interceptor.logging.Level;
import com.jeff.dsource.rxrequest.util.RxScheduler;
import com.jeff.jframework.tools.StringUtils;
import com.jeff.jframework.tools.cache.LruDiskCache;
import com.jeff.jframework.tools.cache.LruMemoryCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Network+Cache数据获取工具类。Retrofit网络请求、rxJava、LruCache组合来实现三级缓存获取数据源
 * 单例模式
 *
 * @author Jeff
 * @date 2020/06/03 14:37
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class RxRequest {
    private static final String TAG = "RxRequest";

    @IntDef({Source.SOURCE_FROM_MEMORY, Source.SOURCE_FROM_DISK, Source.SOURCE_FROM_NETWORK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Source {
        /**
         * 数据来自memory
         */
        int SOURCE_FROM_MEMORY = 0x001;
        /**
         * 数据来自磁盘
         */
        int SOURCE_FROM_DISK = 0x002;
        /**
         * 数据来自网络
         */
        int SOURCE_FROM_NETWORK = 0x003;
    }

    @IntDef({Mode.MODE_NONE_CACHE, Mode.MODE_MEMORY_CACHE_ONLY,
            Mode.MODE_DISK_CACHE_ONLY, Mode.MODE_DOUBLE_CACHE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
        /**
         * 不使用缓存，该模式默认缓存模式
         */
        int MODE_NONE_CACHE = 0x005;
        /**
         * 仅使用memory缓存
         */
        int MODE_MEMORY_CACHE_ONLY = 0x006;
        /**
         * 仅使用磁盘缓存
         */
        int MODE_DISK_CACHE_ONLY = 0x007;
        /**
         * 同时使用memory和磁盘缓存
         */
        int MODE_DOUBLE_CACHE = 0x008;
    }

    private static volatile RxRequest sInstance;

    private Level mLogLevel = Level.BASIC;

    private RxRequest() {
    }

    public static RxRequest getInstance() {
        if (sInstance == null) {
            synchronized (RxRequest.class) {
                if (sInstance == null) {
                    sInstance = new RxRequest();
                }
            }
        }
        return sInstance;
    }

    public void setLogLevel(Level level) {
        this.mLogLevel = level;
    }

    public Level getLogLevel() {
        return this.mLogLevel;
    }

    /**
     * 获取数据源
     *
     * @param builder
     * @param <T>     参数实体类型
     * @return
     */
    public synchronized <T> Disposable get(@NonNull Builder<T> builder) {
        Disposable disposable;
        Observable<Item<T>> memory;
        Observable<Item<T>> disk;
        Cache<T> cacheConfig = builder.getCache();
        switch (cacheConfig.cacheMode) {
            case Mode.MODE_MEMORY_CACHE_ONLY:
                // memory缓存
                memory = loadFromMemory(cacheConfig);
                disposable = concatWithCallback(builder, memory, builder.getNetworkObservable());
                break;
            case Mode.MODE_DISK_CACHE_ONLY:
                // 磁盘缓存
                disk = loadFromDisk(cacheConfig);
                disposable = concatWithCallback(builder, disk, builder.getNetworkObservable());
                break;
            case Mode.MODE_DOUBLE_CACHE:
                //memory缓存+磁盘缓存
                memory = loadFromMemory(cacheConfig);
                disk = loadFromDisk(cacheConfig);
                disposable = concatWithCallback(builder, memory, disk, builder.getNetworkObservable());
                break;
            default:
                disposable = concatWithCallback(builder, builder.getNetworkObservable());
                break;
        }
        return disposable;
    }

    /**
     * 获取数据源
     *
     * @param builder
     * @param <T>     参数实体类型
     * @return
     */
    public synchronized <T> Observable<T> getObservable(@NonNull Builder<T> builder) {
        Observable<Item<T>> memory;
        Observable<Item<T>> disk;
        Cache<T> cacheConfig = builder.getCache();
        switch (cacheConfig.cacheMode) {
            case Mode.MODE_MEMORY_CACHE_ONLY:
                // memory缓存
                memory = loadFromMemory(cacheConfig);
                return concat(builder, memory, builder.getNetworkObservable());
            case Mode.MODE_DISK_CACHE_ONLY:
                // 磁盘缓存
                disk = loadFromDisk(cacheConfig);
                return concat(builder, disk, builder.getNetworkObservable());
            case Mode.MODE_DOUBLE_CACHE:
                //memory缓存+磁盘缓存
                memory = loadFromMemory(cacheConfig);
                disk = loadFromDisk(cacheConfig);
                return concat(builder, memory, disk, builder.getNetworkObservable());
            default:
                return concat(builder, builder.getNetworkObservable());
        }
    }

    /**
     * 获取数据源
     *
     * @param builder
     * @param <T>     参数实体类型
     * @return
     */
    public synchronized <T> void getAndSubscribe(@NonNull Builder<T> builder,
                                        @NonNull RequestObserver<T> observer) {
        Observable<Item<T>> memory;
        Observable<Item<T>> disk;
        Cache<T> cacheConfig = builder.getCache();
        switch (cacheConfig.cacheMode) {
            case Mode.MODE_MEMORY_CACHE_ONLY:
                // memory缓存
                memory = loadFromMemory(cacheConfig);
                concatWithObserver(builder, observer, memory, builder.getNetworkObservable());
                break;
            case Mode.MODE_DISK_CACHE_ONLY:
                // 磁盘缓存
                disk = loadFromDisk(cacheConfig);
                concatWithObserver(builder, observer, disk, builder.getNetworkObservable());
                break;
            case Mode.MODE_DOUBLE_CACHE:
                //memory缓存+磁盘缓存
                memory = loadFromMemory(cacheConfig);
                disk = loadFromDisk(cacheConfig);
                concatWithObserver(builder, observer, memory, disk, builder.getNetworkObservable());
                break;
            default:
                concatWithObserver(builder, observer, builder.getNetworkObservable());
                break;
        }
    }

    /**
     * 数据分级请求，顺序：memory>disk>network
     *
     * @param builder
     * @param sources
     * @param <T>
     * @return
     */
    @SafeVarargs
    private final <T> Observable<T> concat(final Builder<T> builder,
                                           @NonNull Observable<Item<T>>... sources) {
        return RxScheduler.toSubscribe(Observable.concatArray(sources)
                .doOnNext(new Consumer<Item<T>>() {
                    @Override
                    public void accept(Item<T> item) throws Exception {
                        Cache<T> cache = builder.getCache();
                        Log.i(TAG, String.format("#concat doOnNext: tag:[%s] from %s",
                                !StringUtils.isEmpty(cache.key) ? cache.key : "",
                                getSourceAsString(item.source)));
                        if (cache.cacheMode == Mode.MODE_NONE_CACHE) {
                            return;
                        }
                        //缓存数据
                        if (item.data != null) {
                            if (item.source == Source.SOURCE_FROM_NETWORK) {
                                Log.d(TAG, String.format("#concat todo cache:[%s] for %ds",
                                        cache.key,
                                        cache.cacheTime));
                                cache.data = item.data;
                                doCache(cache);
                            } else if (item.source == Source.SOURCE_FROM_DISK) {
                                // no-op
                            }
                        }
                    }
                }).flatMap(new Function<Item<T>, Observable<T>>() {
                    @Override
                    public Observable<T> apply(Item<T> item) throws Exception {
                        return Observable.just(item.data);
                    }
                }));
    }

    /**
     * 数据分级请求，顺序：memory>disk>network
     *
     * @param builder
     * @param sources
     * @param <T>
     * @return
     */
    @SafeVarargs
    private final <T> void concatWithObserver(final Builder<T> builder, RequestObserver<T> observer,
                                              @NonNull Observable<Item<T>>... sources) {
        RxScheduler.toSubscribe(Observable.zip(Observable.just(builder), Observable.concatArray(sources)
                .doOnNext(new Consumer<Item<T>>() {
                    @Override
                    public void accept(Item<T> item) throws Exception {
                        Cache<T> cache = builder.getCache();
                        if (cache.cacheMode == Mode.MODE_NONE_CACHE) {
                            return;
                        }
                        //缓存数据
                        if (item.data != null) {
                            if (item.source == Source.SOURCE_FROM_NETWORK) {
                                Log.d(TAG, String.format("#concatWithCallback todo cache:[%s] for %ds",
                                        cache.key,
                                        cache.cacheTime));
                                cache.data = item.data;
                                doCache(cache);
                            } else if (item.source == Source.SOURCE_FROM_DISK) {
                                // no-op
                            }
                        }
                    }
                }), new BiFunction<Builder<T>, Item<T>, Combine<T>>() {
            @Override
            public Combine<T> apply(Builder<T> b, Item<T> i) throws Exception {
                return new Combine<>(b, i);
            }
        })).subscribe(observer);
    }

    /**
     * 数据分级请求，顺序：memory>disk>network
     *
     * @param builder
     * @param sources
     * @param <T>
     * @return
     */
    @SafeVarargs
    private final <T> Disposable concatWithCallback(final Builder<T> builder,
                                                    @NonNull Observable<Item<T>>... sources) {
        return RxScheduler.toSubscribe(Observable.concatArray(sources)
                .doOnNext(new Consumer<Item<T>>() {
                    @Override
                    public void accept(Item<T> item) throws Exception {
                        Cache<T> cache = builder.getCache();
                        if (cache.cacheMode == Mode.MODE_NONE_CACHE) {
                            return;
                        }
                        //缓存数据
                        if (item.data != null) {
                            if (item.source == Source.SOURCE_FROM_NETWORK) {
                                Log.d(TAG, String.format("#concatWithCallback todo cache:[%s] for %ds",
                                        cache.key,
                                        cache.cacheTime));
                                cache.data = item.data;
                                doCache(cache);
                            } else if (item.source == Source.SOURCE_FROM_DISK) {
                                // no-op
                            }
                        }
                    }
                })).subscribe(new Consumer<Item<T>>() {
            @Override
            public void accept(@NonNull final Item<T> item) throws Exception {
                Cache<T> cache = builder.getCache();
                Log.i(TAG, String.format("#concatWithCallback accept: tag:[%s] from %s",
                        !StringUtils.isEmpty(cache.key) ? cache.key : "", getSourceAsString(item.source)));
                if (builder.getOnResultListener() != null) {
                    builder.getOnResultListener().onResult(item.source, item.data);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                throwable.printStackTrace();
                ResponseException responseException = ExceptionHandler.handleException(throwable);
                Log.e(TAG, String.format("#concatWithCallback error: %d, message: %s",
                        responseException.code, responseException.getMessage()));
                if (builder.getOnResultListener() != null) {
                    builder.getOnResultListener().onError(responseException.code,
                            responseException.message);
                }
            }
        });
    }

    private <T> void doCache(final Cache<T> cache) {
        Single.just(cache)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<Cache<T>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Cache<T> t) {
                        switch (t.cacheMode) {
                            case Mode.MODE_MEMORY_CACHE_ONLY:
                                // memory缓存
//                                LruMemoryCache.getInstance().put(t.key, t.data, t.cacheTime);
                                break;
                            case Mode.MODE_DISK_CACHE_ONLY:
                                // 磁盘缓存
//                                LruDiskCache.getInstance().put(t.key, t.data, t.cacheTime);
                                break;
                            case Mode.MODE_DOUBLE_CACHE:
                                // memory缓存+磁盘缓存
//                                LruDoubleCache.getInstance().put(t.key, t.data, t.cacheTime);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: " + (throwable != null ? throwable.getMessage() : ""));
                    }
                });
    }

    /**
     * 从磁盘加载缓存数据
     *
     * @param cache
     * @param <T>
     * @return
     */
    private <T> Observable<Item<T>> loadFromDisk(final Cache<T> cache) {
        return Observable.create(new ObservableOnSubscribe<Item<T>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Item<T>> emitter) throws Exception {
                Log.i(TAG, String.format("try to load tag:[%s] from disk", cache.key));
                T data = null;
                Log.i(TAG, String.format("[%s] match type...", cache.target.getCanonicalName()));
                if (cache.target == byte[].class) {
                    data = (T) LruDiskCache.getInstance().getBytes(cache.key);
                } else if (cache.target == String.class) {
                    data = (T) LruDiskCache.getInstance().getString(cache.key);
                } else if (cache.target == Bitmap.class) {
                    data = (T) LruDiskCache.getInstance().getBitmap(cache.key);
                } else if (cache.target == Drawable.class) {
                    data = (T) LruDiskCache.getInstance().getDrawable(cache.key);
                } else if (cache.target == JSONObject.class) {
                    data = (T) LruDiskCache.getInstance().getJSONObject(cache.key);
                } else if (cache.target == JSONArray.class) {
                    data = (T) LruDiskCache.getInstance().getJSONArray(cache.key);
                } else {
                    Class<?>[] interfaces = cache.target.getInterfaces();
                    Log.i(TAG, String.format("[%s] match interface type...",
                            cache.target.getCanonicalName()));
                    for (Class<?> inter : interfaces) {
                        Log.i(TAG, String.format("[%s] inter: %s",
                                cache.target.getCanonicalName(), inter.getCanonicalName()));
                        if (inter == Parcelable.class) {
                            Log.i(TAG, String.format("[%s] matched: Parcelable.class",
                                    cache.target.getCanonicalName()));
                            data = LruDiskCache.getInstance().getParcelable(cache.key,
                                    cache.parcelableCreator);
                            break;
                        } else if (inter == Serializable.class) {
                            Log.i(TAG, String.format("[%s] matched: Serializable.class",
                                    cache.target.getCanonicalName()));
                            data = (T) LruDiskCache.getInstance().getSerializable(cache.key);
                            break;
                        }
                    }
                }

                // 在操作符 concat 中，只有调用 onComplete 之后才会执行下一个 Observable
                // 如果缓存数据不为空，则直接读取缓存数据，而不读取网络数据
                Item<T> result = new Item<>();
                if (data != null) {
                    result.source = Source.SOURCE_FROM_DISK;
                    result.data = data;
                    emitter.onNext(result);
                } else {
                    result.source = Source.SOURCE_FROM_NETWORK;
                    emitter.onComplete();
                }
            }
        });
    }

    /**
     * 从memory加载缓存数据
     *
     * @param cache
     * @param <T>
     * @return
     */
    private <T> Observable<Item<T>> loadFromMemory(final Cache<T> cache) {
        return Observable.create(new ObservableOnSubscribe<Item<T>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Item<T>> e) throws Exception {
                Log.i(TAG, String.format("try to load tag:[%s] from memory", cache.key));
                Item<T> result = new Item<>();
                T data = LruMemoryCache.getInstance().get(cache.key);
                // 在操作符 concat 中，只有调用 onComplete 之后才会执行下一个 Observable
                // 如果缓存数据不为空，则直接读取缓存数据，而不读取网络数据
                if (data != null) {
                    result.source = Source.SOURCE_FROM_MEMORY;
                    result.data = data;
                    e.onNext(result);
                } else {
                    result.source = Source.SOURCE_FROM_NETWORK;
                    e.onComplete();
                }
            }
        });
    }

    private String getSourceAsString(int source) {
        switch (source) {
            case Source.SOURCE_FROM_MEMORY:
                return "Memory";
            case Source.SOURCE_FROM_DISK:
                return "Disk";
            case Source.SOURCE_FROM_NETWORK:
                return "Network";
            default:
                return "Unknown";
        }
    }

    public static class Builder<T> {
        /**
         * 缓存
         */
        private Cache<T> cache = Cache.noCache();
        private Observable<Item<T>> networkObservable;
        private OnResultListener<T> onResultListener;

        public Builder() {
        }

        public Cache<T> getCache() {
            return cache;
        }

        public Builder<T> setCache(@NonNull Cache<T> cache) {
            this.cache = cache;
            return this;
        }

        /**
         * 设置网络请求的Observable
         *
         * @param observable
         * @return
         */
        public Builder<T> setNetworkObservable(Observable<T> observable) {
            if (observable == null) return this;
            // 使用map操作符进行数据格式转换
            this.networkObservable = observable.map(new Function<T, Item<T>>() {
                @Override
                public Item<T> apply(T t) throws Exception {
                    Item<T> item = new Item<>();
                    item.source = Source.SOURCE_FROM_NETWORK;
                    item.data = t;
                    return item;
                }
            });
            return this;
        }

        /**
         * 设置获取数据回调
         *
         * @param listener
         * @return
         */
        public Builder<T> setOnResultListener(OnResultListener<T> listener) {
            this.onResultListener = listener;
            return this;
        }

        public Observable<Item<T>> getNetworkObservable() {
            return networkObservable;
        }

        public OnResultListener<T> getOnResultListener() {
            return onResultListener;
        }

        public Builder<T> build() {
            return this;
        }
    }

    public interface OnResultListener<T> {
        /**
         * 获取数据源后的回调
         *
         * @param source 数据源
         * @param data
         */
        void onResult(@Source int source, @Nullable T data);

        /**
         * 数据获取失败的回调
         *
         * @param code
         * @param message
         */
        void onError(int code, String message);
    }

    /**
     * 缓存操作单元
     *
     * @param <T>
     */
    public static class Cache<T> {
        /**
         * 缓存标签，换取缓存时的key值
         */
        public String key;
        /**
         * 缓存模式，默认不缓存
         */
        public @Mode
        int cacheMode = Mode.MODE_NONE_CACHE;
        /**
         * 缓存时间，默认永久
         */
        public int cacheTime = -1;
        /**
         * 缓存内容
         */
        public T data;

        /**
         * 实体类类型
         */
        public Class<T> target;

        /**
         * 只有当实体类是Parcelable才需要该变量
         */
        public Parcelable.Creator<T> parcelableCreator;

        /**
         * 不执行缓存
         *
         * @return
         */
        public static <T> Cache<T> noCache() {
            Cache<T> cache = new Cache<>();
            cache.cacheMode = Mode.MODE_NONE_CACHE;
            return cache;
        }

        public static class Builder<T> {
            /**
             * 缓存标签，换取缓存时的key值
             */
            private String key;
            /**
             * 缓存模式，默认不缓存
             */
            private @Mode
            int cacheMode = Mode.MODE_NONE_CACHE;
            /**
             * 缓存时间，默认永久
             */
            private int cacheTime = -1;
            /**
             * 缓存内容
             */
            private T data;

            /**
             * 实体类类型
             */
            private Class<T> target;

            /**
             * 只有当实体类是Parcelable才需要该变量
             */
            private Parcelable.Creator<T> parcelableCreator;

            /**
             * 设置缓存标签
             *
             * @param key
             * @return
             */
            public Builder<T> setKey(String key) {
                this.key = key;
                return this;
            }

            /**
             * 设置缓存模式
             *
             * @param cacheMode one of {@link Mode}.
             * @return
             */
            public Builder<T> setCacheMode(@Mode int cacheMode) {
                this.cacheMode = cacheMode;
                return this;
            }

            /**
             * 设置缓存时间，以秒为单位
             *
             * @param cacheTime
             * @return
             */
            public Builder<T> setCacheTime(int cacheTime) {
                this.cacheTime = cacheTime;
                return this;
            }

            /**
             * 设置实体类类型
             *
             * @param target
             * @return
             */
            public Builder<T> setTarget(@NonNull Class<T> target) {
                this.target = target;
                return this;
            }

            /**
             * 设置Parcelable实体类类型&设置反序列化使用的Creator
             *
             * @param creator
             * @return
             */
            public Builder<T> setParcelableTarget(@NonNull Class<T> target,
                                                  @NonNull Parcelable.Creator<T> creator) {
                return setTarget(target).setParcelableCreator(creator);
            }

            /**
             * 设置Parcelable反序列化使用的Creator
             *
             * @param creator
             * @return
             */
            public Builder<T> setParcelableCreator(@NonNull Parcelable.Creator<T> creator) {
                this.parcelableCreator = creator;
                return this;
            }

            public Cache<T> apply() {
                Cache<T> cache = new Cache<>();
                cache.cacheMode = this.cacheMode;
                cache.key = this.key;
                cache.cacheTime = this.cacheTime;
                cache.target = this.target;
                cache.parcelableCreator = this.parcelableCreator;
                return cache;
            }

            /**
             * 特定条件下执行缓存
             *
             * @param condition
             * @return
             */
            public Cache<T> cacheIf(boolean condition) {
                if (!condition) {
                    return Cache.noCache();
                } else {
                    return apply();
                }
            }
        }
    }

    /**
     * 数据操作单元
     *
     * @param <T>
     */
    static class Item<T> {
        /**
         * 数据来源
         */
        @Source
        public int source;
        /**
         * 数据
         */
        public T data;

        @Override
        public String toString() {
            return "Item{" +
                    "source=" + source +
                    ", data=" + data +
                    '}';
        }
    }

    static class Combine<T> {
        Builder<T> builder;
        Item<T> item;

        public Combine(Builder<T> builder, Item<T> item) {
            this.builder = builder;
            this.item = item;
        }
    }

    public abstract class RequestObserver<T> extends AbstractObserver<Combine<T>> {

        public RequestObserver() {
            super("Observer");
        }

        public abstract void onResult(@Source int source, @Nullable T data);

        @Override
        public void onNext(Combine<T> response) {
            if (response == null || response.builder == null || response.item == null) {
                onError(new ResponseUnavailableException("Response or builder or item is null!"));
                return;
            }
            Cache<T> cache = response.builder.getCache();
            Log.i(TAG, String.format("accept: tag:[%s] from %s",
                    !StringUtils.isEmpty(cache.key) ? cache.key : "", getSourceAsString(response.item.source)));
            onResult(response.item.source, response.item.data);
        }
    }

}
