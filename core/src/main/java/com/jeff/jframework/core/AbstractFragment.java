package com.jeff.jframework.core;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @deprecated
 * 基于 {@link Fragment} 封装的Fragment基类
 * 建议使用 {@link AbstractV4Fragment}
 * @author Jeff
 * @date 2020/08/04 09:56
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class AbstractFragment extends Fragment {
    protected View rootView;

    public AbstractFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) parent.removeView(rootView);
        } else {
            Preconditions.checkArgument(getLayoutId()!=0,"You should set layout for "+this.getClass().getSimpleName());
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void init(@Nullable Bundle savedInstanceState) {

    }

    protected abstract @LayoutRes int getLayoutId();

    protected abstract void initView();

    protected void initListener(){};

    @SuppressWarnings("unchecked")
    protected <V extends View> V findViewById(@IdRes final int viewId) {
        if (rootView == null) {
            return null;
        }
        return (V) rootView.findViewById(viewId);
    }
}
