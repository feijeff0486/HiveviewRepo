package com.jeff.jframework.rxstealth;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Activity生命周期
 * <p>
 * @author Jeff
 * @date 2019/12/19 17:48
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public interface IActivityLifecycle {
    void onCreate(@Nullable Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
