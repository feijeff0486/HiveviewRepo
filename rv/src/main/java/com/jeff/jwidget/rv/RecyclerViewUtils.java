package com.jeff.jwidget.rv;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.jeff.jframework.core.CannotCreateException;

/**
 * RecyclerView工具类
 * <p>
 * @author Jeff
 * @date 2020/08/01 16:34
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class RecyclerViewUtils {
    private static final String TAG = "RecyclerViewUtils";

    private RecyclerViewUtils() {
        throw new CannotCreateException(getClass());
    }

    /**
     * 获取recyclerView焦点所在的位置
     *
     * @param recyclerView
     * @return
     */
    public static int getFocusChildPosition(RecyclerView recyclerView) {
        if (recyclerView != null && recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager().getFocusedChild() != null) {
            return recyclerView.getLayoutManager().getPosition(recyclerView.getLayoutManager().getFocusedChild());
        }
        return 0;
    }

    /**
     * 请求recyclerView某个position获取焦点
     *
     * @param recyclerView
     * @param position
     */
    public static void requestPositionFocus(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            return;
        }
        requestDelayPositionFocus(recyclerView, position, 0);
    }

    /**
     * 请求recyclerView某个position延迟获取焦点
     *
     * @param recyclerView
     * @param position
     * @param delay
     */
    public static void requestDelayPositionFocus(final RecyclerView recyclerView, final int position, long delay) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView.getLayoutManager() != null) {
                    if (recyclerView.getLayoutManager().findViewByPosition(position) != null &&
                            recyclerView.getLayoutManager().findViewByPosition(position).getVisibility() == View.VISIBLE) {
                        recyclerView.getLayoutManager().findViewByPosition(position).requestFocus();
                        Log.d(TAG, "requestDelayPositionFocus run: position= " + position);
                    }else {
                        int firstVisible=((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        recyclerView.getLayoutManager().findViewByPosition(firstVisible).requestFocus();
                        Log.d(TAG, "requestDelayPositionFocus run: firstVisible= " + firstVisible);
                    }
                }
            }

        }, delay);
    }
}