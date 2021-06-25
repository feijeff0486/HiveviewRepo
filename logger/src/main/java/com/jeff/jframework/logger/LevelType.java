package com.jeff.jframework.logger;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jeff
 * @describe
 * @date 2020/4/1.
 */
@IntDef({LevelType.V, LevelType.D, LevelType.I, LevelType.W, LevelType.E, LevelType.A})
@Retention(RetentionPolicy.SOURCE)
public @interface LevelType {
    int V = Log.VERBOSE;
    int D = Log.DEBUG;
    int I = Log.INFO;
    int W = Log.WARN;
    int E = Log.ERROR;
    int A = Log.ASSERT;
}
