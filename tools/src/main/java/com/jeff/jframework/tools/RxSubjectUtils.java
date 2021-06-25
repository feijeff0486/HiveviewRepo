package com.jeff.jframework.tools;

import android.support.annotation.NonNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * {@link Subject}的使用封装，实现数据共享
 * todo:update
 * @author Jeff
 * @describe
 * @date 2019/6/5.
 */
public final class RxSubjectUtils {
    private static RxSubjectUtils sInstance;

    private RxSubjectUtils() {
    }

    public static RxSubjectUtils getInstance() {
        if (sInstance == null) {
            synchronized (RxSubjectUtils.class) {
                if (sInstance == null) {
                    sInstance = new RxSubjectUtils();
                }
            }
        }
        return sInstance;
    }

    public <T> void publish(T... args) {
        Publish.getInstance().publish(args);
    }

    public <T> void behavior(T... args) {
        Behavior.getInstance().publish(args);
    }

    public <T> void replay(T... args) {
        Replay.getInstance().publish(args);
    }

    public <T> void async(T... args) {
        Async.getInstance().publish(args);
    }

    public <T> void subscribePublish(Observer<T> observable) {
        Publish.getInstance().subscribe(observable);
    }

    public <T> void subscribeBehavior(Observer<T> observable) {
        Behavior.getInstance().subscribe(observable);
    }

    public <T> void subscribeReplay(Observer<T> observable) {
        Replay.getInstance().subscribe(observable);
    }

    public <T> void subscribeAsync(Observer<T> observable) {
        Async.getInstance().subscribe(observable);
    }

    public <T> Disposable subscribePublish(@NonNull Consumer<T> onNext, @NonNull Consumer<Throwable> onError) {
        return Publish.getInstance().subscribe(onNext, onError);
    }

    public <T> Disposable subscribeBehavior(@NonNull Consumer<T> onNext, @NonNull Consumer<Throwable> onError) {
        return Behavior.getInstance().subscribe(onNext, onError);
    }

    public <T> Disposable subscribeReplay(@NonNull Consumer<T> onNext, @NonNull Consumer<Throwable> onError) {
        return Replay.getInstance().subscribe(onNext, onError);
    }

    public <T> Disposable subscribeAsync(@NonNull Consumer<T> onNext, @NonNull Consumer<Throwable> onError) {
        return Async.getInstance().subscribe(onNext, onError);
    }

    public void disposePublish(Disposable disposable) {
        Publish.getInstance().dispose(disposable);
    }

    public void disposeBehavior(Disposable disposable) {
        Behavior.getInstance().dispose(disposable);
    }

    public void disposeReplay(Disposable disposable) {
        Replay.getInstance().dispose(disposable);
    }

    public void disposeAsync(Disposable disposable) {
        Async.getInstance().dispose(disposable);
    }

    public static abstract class AbstractSubject<T> {
        protected Subject<T> mSubject;

        public void subscribe(Observer<T> observable) {
            mSubject.subscribe(observable);
        }

        public Disposable subscribe(@NonNull Consumer<T> onNext, @NonNull Consumer<Throwable> onError) {
            return mSubject.subscribe(onNext, onError);
        }

        public void publish(@NonNull T... args) {
            for (T data : args) {
                mSubject.onNext(data);
            }
        }

        public void dispose(Disposable disposable) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
                disposable = null;
            }
        }
    }

    /**
     * 订阅者只会接受订阅之后的来自PublishSubject发射的数据
     *
     * @param <T>
     */
    public static class Publish<T> extends AbstractSubject<T> {
        private static Publish sInstance;

        private Publish() {
            mSubject = PublishSubject.create();
        }

        public static Publish getInstance() {
            if (sInstance == null) {
                synchronized (Publish.class) {
                    if (sInstance == null) {
                        sInstance = new Publish();
                    }
                }
            }
            return sInstance;
        }
    }

    /**
     * BehaviorSubject会发送离订阅最近的上一个值，没有上一个值的时候会发送默认值（如果有的话)
     *
     * @param <T>
     */
    public static class Behavior<T> extends AbstractSubject<T> {
        private static Behavior sInstance;

        private Behavior() {
            mSubject = BehaviorSubject.create();
        }

        private Behavior(T defaultData) {
            mSubject = BehaviorSubject.createDefault(defaultData);
        }

        public static Behavior getInstance() {
            if (sInstance == null) {
                synchronized (Behavior.class) {
                    if (sInstance == null) {
                        sInstance = new Behavior();
                    }
                }
            }
            return sInstance;
        }

        public static <T> Behavior getInstance(T defaultData) {
            if (sInstance == null) {
                synchronized (Behavior.class) {
                    if (sInstance == null) {
                        sInstance = new Behavior<>(defaultData);
                    }
                }
            }
            return sInstance;
        }
    }

    /**
     * 该Subject会缓存所有的发射数据，无论观察者何时订阅，Subject都会将所有内容发送给订阅者。
     *
     * @param <T>
     */
    public static class Replay<T> extends AbstractSubject<T> {
        private static Replay sInstance;

        private Replay() {
            mSubject = ReplaySubject.create();
        }

        public static Replay getInstance() {
            if (sInstance == null) {
                synchronized (Replay.class) {
                    if (sInstance == null) {
                        sInstance = new Replay();
                    }
                }
            }
            return sInstance;
        }
    }

    /**
     * 使用AsyncSubject无论发送多少个数据事件，观察者永远只能接受到最后一个数据(完成事件必须调用)。
     * 如果发送数据过程中出现错误，观察者仅仅接受到错误信息。
     *
     * @param <T>
     */
    public static class Async<T> extends AbstractSubject<T> {
        private static Async sInstance;

        private Async() {
            mSubject = AsyncSubject.create();
        }

        public static Async getInstance() {
            if (sInstance == null) {
                synchronized (Async.class) {
                    if (sInstance == null) {
                        sInstance = new Async();
                    }
                }
            }
            return sInstance;
        }

        @Override
        public void publish(@NonNull T... args) {
            super.publish(args);
            mSubject.onComplete();
        }
    }
}
