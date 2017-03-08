package cc.easyandroid.easyrecyclerview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cc.easyandroid.easyrecyclerview.animation.BaseAnimation;
import cc.easyandroid.easyrecyclerview.animation.ViewHelper;
import cc.easyandroid.easyrecyclerview.core.IEasyAdapter;
import cc.easyandroid.easyrecyclerview.helper.StickyHeaderHelper;
import cc.easyandroid.easyrecyclerview.items.IFlexible;
import cc.easyandroid.easyrecyclerview.items.IHeader;
import cc.easyandroid.easyrecyclerview.items.RefreshOrLoadmoreFlexible;

public class EasyFlexibleAdapter<T extends IFlexible> extends SelectableAdapter implements IEasyAdapter {
    public static boolean DEBUG = true;

    private static final String TAG = EasyFlexibleAdapter.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    /**
     * The main container for ALL items.
     */
    protected List<T> mItems = new ArrayList<>();
    protected List<IFlexible> mHeaderItems = new ArrayList<>();
    protected List<IFlexible> mFooterItems = new ArrayList<>();
    private IFlexible mLastFooterItem = null;//加载的footer
    private IFlexible mFirstHeaderItem = null;//刷新的header

    private long mDuration = 300L;
    /**
     * The position of the last item that was animated.
     */
    private int mLastAnimatedPosition = -1;

    private Interpolator mInterpolator = new LinearInterpolator();
    /* ViewTypes */
    protected LayoutInflater mInflater;

    private HashMap<Integer, IFlexible> mTypeInstances = new HashMap<>();

    private boolean autoMap = false;

    public OnItemClickListener mItemClickListener;

    public OnItemLongClickListener mItemLongClickListener;

    private StickyHeaderHelper mStickyHeaderHelper;

    private boolean mItemAnimationEnable = true;

    private BaseAnimation mAnimation = null;//

    private boolean headersSticky = false;

    protected OnStickyHeaderChangeListener mStickyHeaderChangeListener;

    private Object mEasyTag;

    /**
     * 如果emptyConditionContainsHeader=ture header.size+item.size>0那么empty就不是空
     */
    private boolean emptyConditionContainsHeader;//empty 的条件是否包含header

    public EasyFlexibleAdapter() {
        this(null);
    }

    public EasyFlexibleAdapter(@Nullable Object listeners) {
        //Create listeners instances
        initializeListeners(listeners);
        //Get notified when items are inserted or removed (it adjusts selected positions)
        registerAdapterDataObserver(new AdapterDataObserver());
    }

    public void setItems(List<T> items) {
        mItems.clear();
        //notifyItemRangeRemoved(getHeaderCount(), oldcount);
        mItems.addAll(items);
        mLastAnimatedPosition = mRecyclerView.getChildCount();
        notifyDataSetChanged();
    }
    public void setItemsAndNotifyChanged(List<T> items) {
        mItems.clear();
        //notifyItemRangeRemoved(getHeaderCount(), oldcount);
        mItems.addAll(items);
        mLastAnimatedPosition = mRecyclerView.getChildCount();
        notifyDataSetChanged();
    }

    public EasyFlexibleAdapter initializeListeners(@Nullable Object listeners) {
        if (listeners instanceof OnItemClickListener)
            mItemClickListener = (OnItemClickListener) listeners;
        if (listeners instanceof OnItemLongClickListener)
            mItemLongClickListener = (OnItemLongClickListener) listeners;
        if (listeners instanceof OnStickyHeaderChangeListener)
            mStickyHeaderChangeListener = (OnStickyHeaderChangeListener) listeners;
        return this;
    }


    public IFlexible getItem(int position) {
        if (position < getFirstHeaderViewCount()) {
            return mFirstHeaderItem;
        } else if (position < getHeaderItemCount() + getFirstHeaderViewCount()) {
            return mHeaderItems.get(position - getFirstHeaderViewCount());
        } else if (position >= getItemCount() - getLastFooterViewCount()) {
            return mLastFooterItem;
        } else if (position >= (getItemCount() - getFooterItemCount() - getLastFooterViewCount())) {
            return mFooterItems.get(position - (getItemCount() - getFooterItemCount() - getLastFooterViewCount()));
        }
        return mItems.get(position - getHeaderItemCount() - getFirstHeaderViewCount());
    }

    public void addFooterViewToLast(View lastFooterView) {
        mLastFooterItem = new RefreshOrLoadmoreFlexible(lastFooterView);
    }

    public void addHeaderViewToFirst(View firstHeaderView) {
        mFirstHeaderItem = new RefreshOrLoadmoreFlexible(firstHeaderView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public boolean isSelectable(int position) {
        return getItem(position).isSelectable();
    }


    @Override
    public int getItemCount() {
        return getHeaderItemCount() + getNormalItemCount() + getFooterItemCount() + getLastFooterViewCount() + getFirstHeaderViewCount();
    }

    public int getNormalItemCount() {
        return mItems.size();
    }

    int getLastFooterViewCount() {
        return mLastFooterItem != null ? 1 : 0;
    }

    int getFirstHeaderViewCount() {
        return mFirstHeaderItem != null ? 1 : 0;
    }

    public int getHeaderItemCount() {
        return mHeaderItems.size();
    }

    public int getFooterItemCount() {
        return mFooterItems.size();
    }

    /**
     * 获取某一个类型的item数量
     *
     * @param viewTypes viewTypes
     * @return count
     */
    public int getItemCountOfTypes(Integer... viewTypes) {
        return getItemCountOfTypesUntil(getItemCount(), viewTypes);
    }


    public int getItemCountOfTypesUntil(@IntRange(from = 0) int position, Integer... viewTypes) {
        List<Integer> viewTypeList = Arrays.asList(viewTypes);
        int count = 0;
        for (int i = 0; i < position; i++) {
            //Privilege faster counting if autoMap is active
            if ((autoMap && viewTypeList.contains(mItems.get(i).getLayoutRes())) ||
                    viewTypeList.contains(getItemViewType(i)))
                count++;
        }
        return count;
    }


    /**
     * 数据是否是空
     *
     * @return
     */
    public boolean isEmpty() {
        if (emptyConditionContainsHeader) {
            return getNormalItemCount() + getHeaderItemCount() == 0;
        }
        return getNormalItemCount() == 0;
    }


    public void setEmptyConditionContainsHeader(boolean emptyConditionContainsHeader) {
        this.emptyConditionContainsHeader = emptyConditionContainsHeader;
    }


    /**
     * item 在adapter的真实位置
     *
     * @param item
     * @return
     */
    public int getGlobalPositionOf(@NonNull IFlexible item) {
        return item != null && mItems != null && !mItems.isEmpty() ? mItems.indexOf(item) + getHeaderItemCount() + getFirstHeaderViewCount() : -1;
    }

    public boolean areHeadersSticky() {
        return headersSticky;
    }

    /**
     * 获取全部悬浮的header
     *
     * @return
     */
    @NonNull
    public List<IHeader> getHeaderItems() {
        List<IHeader> headers = new ArrayList<>();
        for (T item : mItems) {
            if (isHeader(item))
                headers.add((IHeader) item);
        }
        return headers;
    }

    public List<T> getItems() {
        return mItems;
    }

    public boolean isHeader(IFlexible item) {
        return item != null && item instanceof IHeader;
    }

    /**
     * layout id就是item的type
     *
     * @param position position
     * @return viewtype
     */
    @Override
    public int getItemViewType(int position) {
        IFlexible item = getItem(position);
        //Map the view type if not done yet
        mapViewTypeFrom(item);
        autoMap = true;
        return item.getLayoutRes();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IFlexible item = getViewTypeInstance(viewType);
        if (item == null) {
            //If everything has been set properly, this should never happen ;-)
            throw new IllegalStateException("ViewType instance has not been correctly mapped for viewType "
                    + viewType + " or AutoMap is not active: super() cannot be called.");
        }
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        return item.createViewHolder(this, mInflater, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        this.onBindViewHolder(holder, position, Collections.unmodifiableList(new ArrayList<>()));
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

    public void updateItem(@NonNull T item, @Nullable Object payload) {
        updateItem(getGlobalPositionOf(item), item, payload);
    }

    public void updateItem(@IntRange(from = 0) int position, @NonNull T item,
                           @Nullable Object payload) {
        if (position < 0 || position >= mItems.size()) {
            Log.e(TAG, "Cannot updateItem on position out of OutOfBounds!");
            return;
        }
        mItems.set(GlobalPositionToNormalPosition(position), item);
        if (DEBUG) Log.v(TAG, "updateItem notifyItemChanged on position " + position);
        notifyItemChanged(position, payload);
    }

    public int GlobalPositionToNormalPosition(int globalPosition) {
        return globalPosition - getHeaderItemCount() - getFirstHeaderViewCount();
    }

    public boolean addItem(T item) {
        if (item == null) {
            Log.e(TAG, "No items to add!");
            return false;
        }
        if (DEBUG) Log.v(TAG, "addItem delegates addition to addItems!");
        List<T> items = new ArrayList<>(1);
        items.add(item);
        return addItems(items);
    }

    public boolean removeItem(IFlexible item) {
        if (item == null) {
            Log.e(TAG, "No items to remove!");
            return false;
        }
        //Insert Items
        int posion = getGlobalPositionOf(item);
        mItems.remove(item);
        //Notify range addition
        notifyItemRangeRemoved(posion, 1);//
        return true;
    }

    public boolean removeHeaderItem(IFlexible item) {
        if (item == null) {
            Log.e(TAG, "No items to remove!");
            return false;
        }
        //Insert Items
        int posion = getGlobalPositionOf(item);
        mHeaderItems.remove(item);
        //Notify range addition
        notifyItemRangeRemoved(posion, 1);//
        return true;
    }

    public boolean removeItem(int globalPosition) {
        IFlexible item = getItem(globalPosition);
        return removeItem(item);
    }

    public boolean removeHeaderItem(int globalPosition) {
        IFlexible item = getItem(globalPosition);
        return removeHeaderItem(item);
    }

    public boolean addHeaderItem(IFlexible headerItem) {
        if (headerItem == null) {
            Log.e(TAG, "No headerItem to add!");
            return false;
        }
        if (DEBUG) Log.v(TAG, "addItem delegates addition to addHeaderItem!");
        List<IFlexible> headerItems = new ArrayList<>(1);
        headerItems.add(headerItem);
        return addHeaderItems(headerItems);
    }

    public boolean addFooterItem(IFlexible footerItem) {
        if (footerItem == null) {
            Log.e(TAG, "No footerItem to add!");
            return false;
        }
        if (DEBUG) Log.v(TAG, "addItem delegates addition to addFooterItem!");
        List<IFlexible> footerItems = new ArrayList<>(1);
        footerItems.add(footerItem);
        return addFooterItems(footerItems);
    }

    /**
     * @param items items
     */
    public boolean addHeaderItems(List<IFlexible> items) {
        if (items == null || items.isEmpty()) {
            Log.e(TAG, "No items to add!");
            return false;
        }
        //Insert Items
        mHeaderItems.addAll(items);

        //Notify range addition
        notifyItemRangeInserted(getHeaderItemCount() - getFirstHeaderViewCount() - 1, items.size());
        return true;
    }

    /**
     * @param items items
     */
    public boolean addFooterItems(List<IFlexible> items) {
        if (items == null || items.isEmpty()) {
            Log.e(TAG, "No items to add!");
            return false;
        }
        //Insert Items
        mFooterItems.addAll(items);
        //Notify range addition
        notifyItemInserted(getItemCount() - 1);//
        return true;
    }

    /**
     * 添加普通的item
     *
     * @param items items
     * @return boolean
     */
    public boolean addItems(List<T> items) {

        if (items == null || items.isEmpty()) {
            Log.e(TAG, "No items to add!");
            return false;
        }
        //Insert Items
        mItems.addAll(items);
        //Notify range addition
        notifyItemRangeInserted(getItemCount() - items.size() - getFooterItemCount(), items.size());//
        return true;
    }

    public void setItemAnimationEnable(boolean enable) {
        mItemAnimationEnable = enable;
    }

    /**
     * {@inheritDoc}
     *
     * @param position Position of the item to toggle the selection status for.
     * @since 5.0.0-b1
     */
    @Override
    public void toggleSelection(@IntRange(from = 0) int position) {
        IFlexible item = getItem(position);
        //Allow selection only for selectable items
        if (item != null && item.isSelectable()) {
            super.toggleSelection(position);
            updateOnScreenCheckedViews();
        }
    }

    /**
     * 设置动画duration
     *
     * @param duration 时长
     */
    public void setItemAnimationDuration(int duration) {
        mDuration = duration;
    }

    public void setEasyTag(Object easyTag) {
        this.mEasyTag = easyTag;
    }

    public Object getEasyTag() {
        return mEasyTag;
    }

    /**
     * 设置item 动画
     *
     * @param animation animation
     */
    public void setItemAnimation(BaseAnimation animation) {
        mAnimation = animation;
    }

    public void clearItems() {
        if (getNormalItemCount() > 0) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    public EasyFlexibleAdapter setStickyHeaders(boolean headersSticky) {
        // Add or Remove the sticky headers
        if (headersSticky) {
            this.headersSticky = true;
            if (mStickyHeaderHelper == null)
                mStickyHeaderHelper = new StickyHeaderHelper(this, mStickyHeaderChangeListener);
            if (!mStickyHeaderHelper.isAttachedToRecyclerView())
                mStickyHeaderHelper.attachToRecyclerView(mRecyclerView);
            if (mRecyclerView instanceof EasyRecyclerView) {
                EasyRecyclerView easyRecyclerView = (EasyRecyclerView) mRecyclerView;
                easyRecyclerView.addHeaderHeightChangedListener(headerHeightChangedListener);
            }
        } else if (mStickyHeaderHelper != null) {
            this.headersSticky = false;
            mStickyHeaderHelper.detachFromRecyclerView(mRecyclerView);
            if (mRecyclerView instanceof EasyRecyclerView) {
                EasyRecyclerView easyRecyclerView = (EasyRecyclerView) mRecyclerView;
                easyRecyclerView.removeHeaderHeightChangedListener(headerHeightChangedListener);
            }
            mStickyHeaderHelper = null;
        }
        return this;
    }

    EasyRecyclerView.HeaderHeightChangedListener headerHeightChangedListener = new EasyRecyclerView.HeaderHeightChangedListener() {
        @Override
        public void onChanged(int headerHeight) {
            if (mStickyHeaderHelper != null) {
                mStickyHeaderHelper.updateOrClearHeader(false);
                Log.v(TAG, "onChanged  headerHeight =" + headerHeight);
            }
        }
    };


    public IHeader getSectionHeader(@IntRange(from = 0) int position) {
        //Headers are not visible nor sticky
        //When headers are visible and sticky, get the previous header
        for (int i = position; i >= 0; i--) {
            IFlexible item = getItem(i);
            if (isHeader(item)) return (IHeader) item;
        }
        return null;
    }


    public ViewGroup getStickySectionHeadersHolder() {
        ViewGroup viewGroup = (ViewGroup) mRecyclerView.getParent();
        return (ViewGroup) viewGroup.findViewById(R.id.sticky_header_container);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ViewHelper.clear(holder.itemView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        addAnimation(viewHolder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (!autoMap) {
            throw new IllegalStateException("AutoMap is not active: super() cannot be called.");
        }
        //When user scrolls, this line binds the correct selection status
        holder.itemView.setActivated(isSelected(position));
        //Bind the item
        IFlexible item = getItem(position);
        if (item != null) {
            item.bindViewHolder(this, holder, position, payloads);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        if (mStickyHeaderHelper != null) {
            mStickyHeaderHelper.attachToRecyclerView(mRecyclerView);
            if (mRecyclerView instanceof EasyRecyclerView) {
                EasyRecyclerView easyRecyclerView = (EasyRecyclerView) mRecyclerView;
                easyRecyclerView.addHeaderHeightChangedListener(headerHeightChangedListener);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (mStickyHeaderHelper != null) {
            mStickyHeaderHelper.detachFromRecyclerView(mRecyclerView);
            if (mRecyclerView instanceof EasyRecyclerView) {
                EasyRecyclerView easyRecyclerView = (EasyRecyclerView) mRecyclerView;
                easyRecyclerView.removeHeaderHeightChangedListener(headerHeightChangedListener);
            }
            mStickyHeaderHelper = null;
        }
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }


    /**
     * Observer Class responsible to recalculate Selection and Expanded positions.
     */
    private class AdapterDataObserver extends RecyclerView.AdapterDataObserver {


        private void updateOrClearHeader() {
            if (mStickyHeaderHelper != null) {
                mStickyHeaderHelper.updateOrClearHeader(true);
            }
        }

        /* Triggered by notifyDataSetChanged() */
        @Override
        public void onChanged() {
            updateOrClearHeader();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateOrClearHeader();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateOrClearHeader();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            updateOrClearHeader();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            updateOrClearHeader();
        }
    }

    /**
     * 讲type和 t 映射mTypeInstancesz中
     *
     * @param item item
     */
    private void mapViewTypeFrom(IFlexible item) {

        if (item != null && !mTypeInstances.containsKey(item.getLayoutRes())) {
            mTypeInstances.put(item.getLayoutRes(), item);
            if (DEBUG)
                Log.i(TAG, "Mapped viewType " + item.getLayoutRes() + " from " + item.getClass().getSimpleName());
        }
    }

    public boolean isEnabled(int position) {
        IFlexible item = getItem(position);
        return item != null && item.isEnabled();
    }

    /**
     * 根据view type 获取对应的对象
     *
     * @param viewType viewType
     * @return T
     */
    private IFlexible getViewTypeInstance(int viewType) {
        return mTypeInstances.get(viewType);
    }


    public interface OnItemClickListener {

        boolean onItemClick(int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(int position);
    }

    /**
     * @since 05/03/2016
     */
    public interface OnStickyHeaderChangeListener {
        /**
         * Called when the current sticky header changed.
         *
         * @param sectionIndex the position of header, -1 if no header is sticky
         * @since 5.0.0-b1
         */
        void onStickyHeaderChange(int sectionIndex);
    }
}