package com.jeff.jwidget.rv;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeff.jframework.core.ContextUtils;
import com.jeff.jframework.core.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView#Adapter封装
 * <p>
 *
 * @author Jeff
 * @date 2020/08/01 16:34
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class AbstractAdapter<M, H extends BaseViewHolder<M>> extends RecyclerView.Adapter<H> {
    private static final String TAG = "AbstractAdapter";

    protected static final int VIEW_TYPE_EMPTY = 0xfff0;
    protected static final int VIEW_TYPE_DEFAULT = 0xfff1;

    private final SparseArray<View> mViewArray = new SparseArray<>();
    protected List<M> mData=new ArrayList<>();
    protected Context mContext;
    protected ViewGroup mParent;
    private LayoutInflater mInflater;

    private OnItemListener.OnItemClickListener mClickListener;
    private OnItemListener.OnItemLongClickListener mLongClickListener;
    private OnItemListener.OnItemFocusChangedListener<H> mFocusChangeListener;
    private OnItemListener.OnItemSelectListener mSelectListener;
    private OnItemListener.OnItemKeyListener mKeyListener;

    private boolean logging=false;
    private boolean enableEmptyView = false;

    public AbstractAdapter() {
        this(false);
    }

    public AbstractAdapter(List<M> data,boolean enableEmptyView) {
        mData = data;
        this.enableEmptyView=enableEmptyView;
        setHasStableIds(true);
    }

    public AbstractAdapter(boolean enableEmptyView) {
        this.enableEmptyView=enableEmptyView;
        setHasStableIds(true);
    }

    public void setData(@NonNull final List<M> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void refreshWithAutoRequestFocus(@NonNull final List<M> data) {
        mData = data;
        notifyItemRangeChanged(0, data.size());
    }

    public void addData(@NonNull final List<M> data) {
        mData.addAll(data);
        notifyItemRangeChanged(getDataSize(), data.size());
    }

    public void addItem(int position, M item) {
        mData.add(position, item);
        notifyItemInserted(position);
        //保证position刷新
        notifyItemRangeInserted(position, getDataSize() - position);
    }

    public void addItem(M item) {
        int position = getDataSize();
        addItem(position, item);
    }

    public void remove(int position) {
        if (mData == null) return;
        mData.remove(position);
        notifyItemRemoved(position);
        //保证position刷新
        notifyItemRangeChanged(position, getDataSize() - position);
    }

    public void clearData() {
        if (mData == null) return;
        mData.clear();
        notifyDataSetChanged();
    }

    public void move(int position, int newPosition) {
        if (mData == null) return;
        M data = mData.remove(position);
        mData.add(newPosition, data);
        notifyItemMoved(position, newPosition);
        //保证position刷新
        notifyItemRangeChanged(0, getDataSize());
    }

    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = getItemView(parent, viewType);
        if (viewType==VIEW_TYPE_EMPTY){
            if (enableEmptyView){
                Preconditions.checkArgument(itemView!=null,"Please setup layout for viewType: "+viewType);
            }else {
                return null;
            }
            return (H) new BaseViewHolder<M>(itemView){
                @Override
                public void bind(M data, int position) {

                }

                @Override
                public void release(int position) {

                }
            };
        }else {
            Preconditions.checkArgument(itemView!=null,"Please setup layout for viewType: "+viewType);
        }
        return createViewHolder(itemView, viewType);
    }

    /**
     * 创建新的ViewHolder
     *
     * @param itemView
     * @param viewType
     * @return
     */
    protected abstract H createViewHolder(View itemView, int viewType);

    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        if (getItemViewType(position)==VIEW_TYPE_EMPTY){
            return;
        }
        bindCustomViewHolder(holder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull H holder, int position,
                                 @NonNull List<Object> payloads) {
        log("onBindViewHolder", "position:" + position);
        if (payloads.isEmpty()) {
            log("onBindViewHolder", "Empty payloads list.");
            super.onBindViewHolder(holder, position, payloads);
            return;
        }
        log("onBindViewHolder", "current:" + getItem(position).toString());
        if (payloads.size() > 0 && payloads.get(0) != null) {
            log("onBindViewHolder", "apply payload.");
            applyPayload(holder, position, payloads.get(0));
            log("onBindViewHolder", "after:" + getItem(position).toString());
        }
    }

    protected void bindCustomViewHolder(final H holder, final int position) {
        if (mClickListener != null) {
            holder.getBindListenerView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, position);
                }
            });
        }
        if (mLongClickListener != null) {
            holder.getBindListenerView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mLongClickListener.onItemLongClick(v, position);
                }
            });
        }
        holder.getBindListenerView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mFocusChangeListener != null) {
                    mFocusChangeListener.onItemFocusChanged(v, hasFocus, holder, position);
                }
                if (mSelectListener != null) {
                    if (hasFocus) {
                        mSelectListener.onItemSelected(v, position);
                    } else {
                        mSelectListener.onItemUnSelected(v, position);
                    }
                }
            }
        });
        if (mKeyListener != null) {
            holder.getBindListenerView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return mKeyListener.onKey(v, keyCode, event, position);
                }
            });
        }
        bind(holder, mData.get(position), position);
    }

    /**
     * ViewHolder绑定数据
     *
     * @param holder
     * @param data
     * @param position
     */
    protected void bind(final H holder, final M data, final int position) {
        if (data != null) {
            log(holder.getClass().getSimpleName() + "bind", "position= " + position);
            holder.bind(data, position);
            holder.setRecycled(false);
        } else {
            Log.e(TAG, holder.getClass().getSimpleName() + " bind: position= " + position + ", data is null!");
        }
    }

    private void applyPayload(H holder, int position, Object payload) {
        onItemApplyPayload(getItem(position),payload);
        holder.applyPayload(payload);
    }

    protected void onItemApplyPayload(M data, Object payload){}

    @Override
    public int getItemViewType(int position) {
        if (enableEmptyView&&getDataSize()==0){
            return VIEW_TYPE_EMPTY;
        } else {
            return getCustomViewType(position);
        }
    }

    protected int getCustomViewType(final int position) {
        return VIEW_TYPE_DEFAULT;
    }

    /**
     * 获取itemView
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected View getItemView(ViewGroup parent, int viewType) {
        if (mParent == null) {
            mParent = parent;
            mContext = parent.getContext();
            mInflater = LayoutInflater.from(mContext);
        }
        View itemView = mViewArray.get(viewType);
        if (itemView == null) {
            if (bindLayout(viewType) != 0) {
                itemView = inflateLayout(bindLayout(viewType));
                log(this.getClass().getSimpleName() + "getItemView", "bindLayout");
            } else {
                itemView = bindView(viewType);
                if (itemView == null) {
                    throw new IllegalArgumentException(this.getClass().getName() + " has not bind view by method #bindLayout or #bindView.");
                }
                log(this.getClass().getSimpleName() + "getItemView", "bindView");
            }
            if (itemView!=null){
                mViewArray.put(viewType,itemView);
            }
        }
        return itemView;
    }

    /**
     * 绑定布局文件
     * 同样也可以通过{@link #bindView(int)}来绑定自定义的View
     *
     * @param viewType
     * @return
     */
    protected abstract int bindLayout(final int viewType);

    /**
     * 绑定代码中自定义的View
     *
     * @param viewType
     * @return
     */
    protected View bindView(int viewType) {
        return null;
    }

    /**
     * 根据布局id获取View
     *
     * @param layoutId
     * @return
     */
    private View inflateLayout(@LayoutRes final int layoutId) {
        return mInflater.inflate(layoutId, mParent, false);
    }

    public int getDataSize() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemCount() {
        if (enableEmptyView&&getDataSize()==0)return 1;
        return getDataSize();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull H holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.isRecycled()) {
            onBindViewHolder(holder, holder.getAdapterPosition());
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull H holder) {
        super.onViewDetachedFromWindow(holder);
        log("onViewDetachedFromWindow", holder.getClass().getSimpleName()+" release: position= " + holder.getAdapterPosition());
        holder.release(holder.getAdapterPosition());
        holder.setRecycled(true);
    }

    public M getItem(int position) {
        if (mData==null)return null;
        return mData.get(position);
    }

    protected int getColor(int resId) {
        return ContextUtils.getColor(resId);
    }

    protected String getString(int resId) {
        return ContextUtils.getString(resId);
    }

    public void setOnItemClickListener(OnItemListener.OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnItemLongClickListener(OnItemListener.OnItemLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    public void setOnFocusChangeListener(OnItemListener.OnItemFocusChangedListener<H> focusChangeListener) {
        mFocusChangeListener = focusChangeListener;
    }

    public void setOnSelectListener(OnItemListener.OnItemSelectListener selectListener) {
        this.mSelectListener = selectListener;
    }

    public void setOnKeyListener(OnItemListener.OnItemKeyListener keyListener) {
        this.mKeyListener = keyListener;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    void log(String methodTag, String content) {
        if (logging) {
            Log.d(TAG, "[" + methodTag + "]: " + content);
        }
    }
}