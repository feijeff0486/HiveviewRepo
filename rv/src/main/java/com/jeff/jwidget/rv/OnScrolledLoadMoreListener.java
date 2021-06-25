package com.jeff.jwidget.rv;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * 滚动加载更多
 * @see RecyclerView.OnScrollListener
 * <p>
 * @author Jeff
 * @date 2020/08/01 16:34
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class OnScrolledLoadMoreListener extends RecyclerView.OnScrollListener {
    private static final String TAG = OnScrolledLoadMoreListener.class.getSimpleName();

    private GridLayoutManager mLayoutManager;
    /**
     * 分页请求标记，从0开始
     * 第一次加载数据时会调用一次{@link #onLoadMore(int)}
     */
    private int mCurrentPageIndex = 0;
    /**
     * 主要用来存储上一个totalItemCount
     */
    private int mPreviousTotal = 0;
    /**
     * 是否正在上拉加载数据
     */
    private boolean isLoading = true;

    public OnScrolledLoadMoreListener(GridLayoutManager linearLayoutManager) {
        this.mLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //在屏幕上可见的item数量
        int visibleItemCount = recyclerView.getChildCount();
        //已经加载出来的Item数量
        int totalItemCount = mLayoutManager.getItemCount();
        //在屏幕可见的Item中的第一个
        int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
        //在屏幕可见的Item中的最后一个
        int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        if (isLoading) {
            Log.d(TAG, "onScrolled: firstVisibleItem= " + firstVisibleItem + ", totalItemCount= " +
                    totalItemCount + ", visibleItemCount= " + visibleItemCount);
            if (totalItemCount > mPreviousTotal) {
                //说明数据已经加载结束
                isLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }
        //当倒数第二行可见时，加载更多
        if (!isLoading && totalItemCount - getLastLineCount() <= lastVisibleItem + 1) {
//            Log.i(TAG, "onScrolled: totalItemCount - getLastLineCount()= " + (totalItemCount - getLastLineCount()) +
//                    ", lastVisibleItem + 1= " + (lastVisibleItem + 1));
            mCurrentPageIndex++;
            onLoadMore(mCurrentPageIndex);
            isLoading = true;
        }
    }

    /**
     * 获取最后一行的item个数
     */
    private int getLastLineCount() {
        if (mLayoutManager.getItemCount() == 0) {
            return 0;
        }
        int lastLineCount = mLayoutManager.getItemCount() % mLayoutManager.getSpanCount();
        if (lastLineCount == 0) {
            lastLineCount = mLayoutManager.getSpanCount();
        }
        return lastLineCount;
    }

    /**
     * 加载更多
     *
     * @param currentPageIndex 当前页
     */
    public abstract void onLoadMore(int currentPageIndex);

    /**
     * 当重新搜索数据时，需要重置下面值
     */
    public void back2Init() {
        mPreviousTotal = 0;
        mCurrentPageIndex = 0;
    }
}