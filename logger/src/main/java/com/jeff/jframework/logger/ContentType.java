package com.jeff.jframework.logger;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jeff
 * @describe
 * @date 2020/4/1.
 */
@IntDef({ContentType.FILE, ContentType.JSON, ContentType.XML})
@Retention(RetentionPolicy.SOURCE)
@interface ContentType {
    int FILE = 0x10;
    int JSON = 0x20;
    int XML  = 0x30;
}