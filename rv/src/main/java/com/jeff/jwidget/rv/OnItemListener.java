package com.jeff.jwidget.rv;

import android.view.KeyEvent;
import android.view.View;

/**
 * Item事件回调接口
 * @author Jeff
 * @date 2020/08/01 16:33
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public interface OnItemListener {
    interface OnItemClickListener {
        void onItemClick(final View view, final int position);
    }

    interface OnItemFocusChangedListener<H extends BaseViewHolder> {
        void onItemFocusChanged(final View view, final boolean hasFocus, final H viewHolder, final int position);
    }

    interface OnItemKeyListener {
        boolean onKey(final View view, final int keyCode, final KeyEvent event, final int position);
    }

    interface OnItemLongClickListener {
        boolean onItemLongClick(final View view, final int position);
    }

    interface OnItemSelectListener {
        void onItemSelected(final View view, final int position);

        void onItemUnSelected(final View view, final int position);
    }
}
