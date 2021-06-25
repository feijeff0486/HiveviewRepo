package com.jeff.jframework.tools.preference;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jeff
 * @describe
 * @date 2020/3/27.
 */
@IntDef({DataType.INTEGER, DataType.LONG, DataType.BOOLEAN, DataType.FLOAT, DataType.STRING, DataType.STRING_SET})
@Retention(RetentionPolicy.SOURCE)
public @interface DataType {
    int INTEGER = 0x1;
    int LONG = 0x2;
    int BOOLEAN = 0x3;
    int FLOAT = 0x4;
    int STRING = 0x5;
    int STRING_SET = 0x6;
}