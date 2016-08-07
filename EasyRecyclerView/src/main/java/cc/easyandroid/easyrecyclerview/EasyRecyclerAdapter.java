package cc.easyandroid.easyrecyclerview;

import android.animation.Animator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import cc.easyandroid.easyrecyclerview.animation.AlphaInAnimation;
import cc.easyandroid.easyrecyclerview.animation.BaseAnimation;
import cc.easyandroid.easyrecyclerview.animation.SlideInBottomAnimation;
import cc.easyandroid.easyrecyclerview.animation.SlideInLeftAnimation;

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
    private boolean mOpenAnimationEnable = true;
    private int mDuration = 400;
    /**
     * The position of the last item that was animated.
     */
    private int mLastAnimatedPosition = -1;
    private Interpolator mInterpolator = new LinearInterpolator();
    private OnItemClickListener<T> mListener;

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

    public void addDatas(List<T> datas) {
        mDatas.addAll(datas);
        notifyItemRangeInserted(getItemCount() - datas.size() - getFooterCount(), datas.size());//
    }

    public void setDatas(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearDatas() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {//type 包括 index 和 和type
        if (position < getHeaderCount()) {
            return TYPE_HEADER | position;
        } else if (position >= (getHeaderCount() + getNormalItemCount())) {
            return TYPE_FOOTER | (position - (getHeaderCount() + getNormalItemCount()));
        }
        return onCreatItemViewType(position - getHeaderCount());
    }

    /**
     * 给用户重新item 的机会
     *
     * @param position 在data数据集合的位置
     * @return type
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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (isHeaderType(getItemViewType(position))) return;//header
        if (position >= (mHeaderViews.size() + mDatas.size())) {//
            if (isFooterType(getItemViewType(position))) return;//footer
        }
        final int pos = getRealPosition(viewHolder);
        final T data = mDatas.get(pos);
        onBind(viewHolder, pos, data);
        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, data);
                }
            });
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

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
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int viewType = holder.getItemViewType();
        if (isHeaderType(viewType) || isFooterType(viewType)) {
            setFullSpan(holder);
        } else {
            addAnimation(holder);
        }
    }

    BaseAnimation animation = new AlphaInAnimation();

    /**
     * add animation when you want to show time
     *
     * @param holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (mOpenAnimationEnable && position > mLastAnimatedPosition) {
            for (Animator anim : animation.getAnimators(holder.itemView)) {
                startAnim(anim, position);
            }

        }
    }

    /**
     * set anim to start when loading
     *
     * @param anim
     * @param position
     */
    protected void startAnim(Animator anim, int position) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
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

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
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

    public abstract void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, T data);

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
        void onItemClick(int position, T data);
    }

    public boolean isEmpty() {
        return getNormalItemCount() == 0;
    }

}
