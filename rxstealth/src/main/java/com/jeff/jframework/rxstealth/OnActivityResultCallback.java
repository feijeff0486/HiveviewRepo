package com.jeff.jframework.rxstealth;

import android.content.Intent;

/**
 * 链式跳转回调接口
 * <p>
 * @author Jeff
 * @date 2019/12/19 17:49
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public interface OnActivityResultCallback {
    void callback(int requestCode, int resultCode, Intent data);
}
