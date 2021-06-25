package com.jeff.jwidget.rv;

import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;

/**
 *
 * <p>
 * @author Jeff
 * @date 2020/08/01 16:34
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class BaseViewHolder<M> extends RecyclerView.ViewHolder {
    private SparseArray<View> viewArray = new SparseArray<>();
    private boolean isRecycled=false;

    public BaseViewHolder(View itemView) {
        this(itemView, false);
    }

    public BaseViewHolder(View itemView, boolean setupWave) {
        super(itemView);
        if (setupWave) {
            setupWaveItem(itemView);
        }
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V findViewById(@IdRes final int viewId) {
        View view = viewArray.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            viewArray.put(viewId, view);
        }
        return (V) view;
    }

    /**
     * 获取绑定listener的view，默认为itemView
     * 可在子类重写该方法进行修改
     * @return
     */
    public View getBindListenerView(){
        return itemView;
    }

    /**
     * 绑定数据到View
     */
    public abstract void bind(M data, int position);

    /**
     * 释放资源
     * @param position
     */
    public abstract void release(int position);

    /**
     * 设置水波纹背景
     */
    private void setupWaveItem(View itemView) {
        if (itemView.getBackground() == null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = itemView.getContext().getTheme();
            int top = itemView.getPaddingTop();
            int bottom = itemView.getPaddingBottom();
            int left = itemView.getPaddingLeft();
            int right = itemView.getPaddingRight();
            if (theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)) {
                itemView.setBackgroundResource(typedValue.resourceId);
            }
            itemView.setPadding(left, top, right, bottom);
        }
    }

    public boolean isRecycled() {
        return isRecycled;
    }

    /**
     * set false when call {@link #bind(M, int)} and true when call {@link #release(int)}
     * @param recycled
     */
    public void setRecycled(boolean recycled) {
        isRecycled = recycled;
    }

    public void setOnClickListener(@IdRes final int viewId, View.OnClickListener listener) {
        findViewById(viewId).setOnClickListener(listener);
    }

    public void setOnLongClickListener(@IdRes final int viewId, View.OnLongClickListener listener) {
        findViewById(viewId).setOnLongClickListener(listener);
    }

    public void applyPayload(Object payload) {

    }
}