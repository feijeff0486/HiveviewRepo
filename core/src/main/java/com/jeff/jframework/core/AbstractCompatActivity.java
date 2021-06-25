package com.jeff.jframework.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 基于{@link AppCompatActivity}封装的Activity基类
 *
 * @author Jeff
 * @date 2021/1/8
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class AbstractCompatActivity extends AppCompatActivity {

    @IntDef({IntentFrom.FROM_ONCREATE, IntentFrom.FROM_ONNEWINTENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntentFrom {
        int FROM_ONCREATE = 0x0;
        int FROM_ONNEWINTENT = 0x1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preconditions.checkArgument(getLayoutId() != 0,
                "You had not set layout resource id for " + this.getClass().getName());
        setContentView(getLayoutId());
        handleExtraParams(IntentFrom.FROM_ONCREATE);
        init(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleExtraParams(IntentFrom.FROM_ONNEWINTENT);
    }

    /**
     * 设置activity的布局id
     *
     * @return
     */
    protected abstract @LayoutRes
    int getLayoutId();

    /**
     * 获取外部通过intent传递的参数
     *
     * @param from˙
     */
    protected abstract void handleExtraParams(@IntentFrom int from);

    protected void init(@Nullable Bundle savedInstanceState) {
    }

    /**
     * 在这里初始化view
     */
    protected abstract void initView();

    protected void initListener(){}
}
