package cc.easyandroid.easyrecyclerview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import cc.easyandroid.easyrecyclerview.animation.BaseAnimation;
import cc.easyandroid.easyrecyclerview.animation.ViewHelper;

/**
 * Created by cgp
 */
public abstract class EasyRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_HEADER = 1 << 24;//2 24 header item
    public static final int TYPE_FOOTER = 1 << 25;//2 25 foot item

    protected ArrayList<T> mDatas = new ArrayList<>();
    protected ArrayList<View> mHeaderViews = new ArrayList<>();
    protected ArrayList<View> mFooterViews = new ArrayList<>();
    private View mLastFooterView = null;//放在最后的footview
    private boolean mItemAnimationEnable = true;
    private BaseAnimation mAnimation = null;//
    private long mDuration = 300L;
    /**
     * The position of the last item that was animated.
     */
    private int mLastAnimatedPosition = -1;
    private Interpolator mInterpolator = new LinearInterpolator();
    private OnItemClickListener<T> mListener;
    protected RecyclerView mRecyclerView;

    public void setOnItemClickListener(OnItemClickListener<T> li) {
        mListener = li;
    }

    public void addHeaderView(View headerView) {
        mHeaderViews.add(headerView);
        notifyItemInserted(mHeaderViews.size() - 1);
    }

    public void addFooterView(View footerView) {
        mFooterViews.add(footerView);
        notifyItemInserted(getItemCount() - 1);//
    }

    void addFooterViewToLast(View lastFooterView) {
        mLastFooterView = lastFooterView;
    }

    void addHeaderViewToFirst(View firstHeaderView) {
        mHeaderViews.add(0, firstHeaderView);
        notifyItemInserted(0);
    }

    public void setItemAnimationEnable(boolean enable) {
        mItemAnimationEnable = enable;
    }

    /**
     * 设置动画duration
     *
     * @param duration 时长
     */
    public void setItemAnimationDuration(int duration) {
        mDuration = duration;
    }

    /**
     * 设置item 动画
     *
     * @param animation animation
     */
    public void setItemAnimation(BaseAnimation animation) {
        mAnimation = animation;
    }

    public void addDatas(List<T> datas) {
        mDatas.addAll(datas);
        notifyItemRangeInserted(getItemCount() - datas.size() - getFooterCount(), datas.size());//
    }

    public void setDatas(List<T> datas) {
        //int oldcount = mDatas.size();
        mDatas.clear();
        //notifyItemRangeRemoved(getHeaderCount(), oldcount);
        mDatas.addAll(datas);
        notifyDataSetChanged();
        mLastAnimatedPosition = mRecyclerView.getChildCount();
    }

    public void clearDatas() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {//type 包括 index 和 和type
        if (position < getHeaderCount()) {
            return TYPE_HEADER | position;
        } else if (position >= (getItemCount() - getFooterCount() - getLastFooterViewCount())) {
            return TYPE_FOOTER | (position - (getHeaderCount() + getNormalItemCount()));
        }
        return onCreatItemViewType(position - getHeaderCount());
    }

    /**
     * 给用户重新item 的机会
     *
     * @param position 在data数据集合的位置
     * @return position  position
     */
    public int onCreatItemViewType(int position) {
        return TYPE_NORMAL;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if ((viewType & TYPE_HEADER) == TYPE_HEADER) {
            return new Holder(mHeaderViews.get((viewType ^ TYPE_HEADER)));//header
        } else if ((viewType & TYPE_FOOTER) == TYPE_FOOTER) {
            int index = viewType ^ TYPE_FOOTER;
            if (index >= mFooterViews.size()) {
                return new FooterHolder(mLastFooterView);// footer
            } else {
                return new HeaderHolder(mFooterViews.get((viewType ^ TYPE_FOOTER)));// footer
            }
        }
        return onCreate(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (isHeaderType(getItemViewType(position))) return;//header
        if (position >= (mHeaderViews.size() + mDatas.size())) {//
            if (isFooterType(getItemViewType(position))) return;//footer
        }
        onBind(viewHolder, position);
        if (mListener != null) {
            final View clickView = viewHolder.itemView;
            clickView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performItemClick(clickView, position);
                }
            });
        }
    }

    public boolean performItemClick(View view, int position) {
        if (position >= 0 && position < getItemCount()) {
            mListener.onItemClick(this,view, position);
            return true;
        }
        return false;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return isHeaderType(getItemViewType(position)) || isFooterType(getItemViewType(position)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    /**
     * 是否是header type
     *
     * @param viewType viewType
     * @return isHeaderType
     */
    protected boolean isHeaderType(int viewType) {
        return (viewType & TYPE_HEADER) == TYPE_HEADER;
    }

    /**
     * 是否是footer type
     *
     * @param viewType viewType
     * @return isFooterType
     */
    protected boolean isFooterType(int viewType) {
        return (viewType & TYPE_FOOTER) == TYPE_FOOTER;
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        int viewType = viewHolder.getItemViewType();
        if (isHeaderType(viewType) || isFooterType(viewType)) {
            setFullSpan(viewHolder);
        } else {
            addAnimation(viewHolder);
        }
    }

    /**
     * add animation when you want to show time
     *
     * @param holder holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (mItemAnimationEnable && position > mLastAnimatedPosition) {
            if (mAnimation != null) {
                Animator[] animators = mAnimation.getAnimators(holder.itemView);
                startAnim(animators, position);
            }

        }
    }


    /**
     * set anim to start when loading
     *
     * @param animators animators
     * @param position  position
     */
    protected void startAnim(Animator[] animators, int position) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.setInterpolator(mInterpolator);
        set.setStartDelay(0);
        set.setDuration(mDuration);
        set.start();
        mLastAnimatedPosition = position;
    }


    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return position - mHeaderViews.size();
    }

    /**
     * 根据positon位置获取对应的对象值
     *
     * @param position
     * @return
     */
    public T getData(int position) {
        int realPosition = position - mHeaderViews.size();
        if (realPosition >= 0 && realPosition < getNormalItemCount()) {
            return mDatas.get(realPosition);
        }
        return null;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ViewHelper.clear(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + mHeaderViews.size() + mFooterViews.size() + getLastFooterViewCount();
    }

    int getLastFooterViewCount() {
        return mLastFooterView != null ? 1 : 0;
    }

    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    public int getFooterCount() {
        return mFooterViews.size();
    }

    public int getNormalItemCount() {
        return mDatas.size();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, final int viewType);

    public abstract void onBind(RecyclerView.ViewHolder viewHolder, int adapterPosition);

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(EasyRecyclerAdapter<T> adapter, View view, int position);
    }

    public boolean isEmpty() {
        return getNormalItemCount() == 0;
    }

}
