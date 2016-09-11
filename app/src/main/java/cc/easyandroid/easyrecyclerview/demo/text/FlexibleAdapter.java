package cc.easyandroid.easyrecyclerview.demo.text;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cc.easyandroid.easyrecyclerview.IEasyAdapter;
import cc.easyandroid.easyrecyclerview.demo.R;

public class FlexibleAdapter<T extends IFlexible> extends RecyclerView.Adapter implements IEasyAdapter {
    public static boolean DEBUG = true;

    private static final String TAG = FlexibleAdapter.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    /**
     * The main container for ALL items.
     */
    private List<T> mItems = new ArrayList<>();
    private List<IFlexible> mHeaderItems = new ArrayList<>();
    private List<IFlexible> mFooterItems = new ArrayList<>();
    private IFlexible mLastFooterItem = null;//加载的footer
    private IFlexible mFirstHeaderItem = null;//刷新的header

    /* ViewTypes */
    protected LayoutInflater mInflater;

    private HashMap<Integer, IFlexible> mTypeInstances = new HashMap<>();
    private boolean autoMap = false;

    public OnItemClickListener mItemClickListener;

    public OnItemLongClickListener mItemLongClickListener;

    private StickyHeaderHelper mStickyHeaderHelper;

    private int minCollapsibleLevel = 0, selectedLevel = -1;

    public FlexibleAdapter() {
        this(null);
    }

    private boolean headersSticky = false;
    protected OnStickyHeaderChangeListener mStickyHeaderChangeListener;

    private boolean scrollOnExpand = false, collapseOnExpand = false,
            childSelected = false, parentSelected = false;

    public FlexibleAdapter(@Nullable Object listeners) {
        //Create listeners instances
        initializeListeners(listeners);
        //Get notified when items are inserted or removed (it adjusts selected positions)
    }

    public void setItems(List<T> items) {
        mItems.clear();
        //notifyItemRangeRemoved(getHeaderCount(), oldcount);
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public FlexibleAdapter initializeListeners(@Nullable Object listeners) {
        if (listeners instanceof OnItemClickListener)
            mItemClickListener = (OnItemClickListener) listeners;
        if (listeners instanceof OnItemLongClickListener)
            mItemLongClickListener = (OnItemLongClickListener) listeners;
        if (listeners instanceof OnStickyHeaderChangeListener)
            mStickyHeaderChangeListener = (OnStickyHeaderChangeListener) listeners;
        return this;
    }


    public final IFlexible getItem(int position) {
        if (position < getFirstHeaderViewCount()) {
            return mFirstHeaderItem;
        } else if (position < getHeaderItemCount() + getFirstHeaderViewCount()) {
            return mHeaderItems.get(position + getFirstHeaderViewCount());
        } else if (position >= getItemCount() - getLastFooterViewCount()) {
            return mLastFooterItem;
        } else if (position > (getItemCount() - getFooterItemCount() - getLastFooterViewCount())) {
            return mFooterItems.get(position - (getHeaderItemCount() + getItemCount()));
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mStickyHeaderHelper != null) {
            mStickyHeaderHelper.attachToRecyclerView(mRecyclerView);
        }
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (mStickyHeaderHelper != null) {
            mStickyHeaderHelper.detachFromRecyclerView(mRecyclerView);
            mStickyHeaderHelper = null;
        }
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    @Override
    public final int getItemCount() {
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


    public boolean isEmpty() {
        return getNormalItemCount() == 0;
    }


    public int getGlobalPositionOf(@NonNull IFlexible item) {
        return item != null && mItems != null && !mItems.isEmpty() ? mItems.indexOf(item) : -1;
    }


    @NonNull
    public List<IHeader> getHeaderItems() {
        List<IHeader> headers = new ArrayList<>();
        for (T item : mItems) {
            if (isHeader(item))
                headers.add((IHeader) item);
        }
        return headers;
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


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (!autoMap) {
            throw new IllegalStateException("AutoMap is not active: super() cannot be called.");
        }
        //Bind the item
        IFlexible item = getItem(position);
        if (item != null) {
            item.bindViewHolder(this, holder, position, payloads);
        }
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

    public boolean addHeaderItem(T headerItem) {
        if (headerItem == null) {
            Log.e(TAG, "No headerItem to add!");
            return false;
        }
        if (DEBUG) Log.v(TAG, "addItem delegates addition to addHeaderItem!");
        List<T> headerItems = new ArrayList<>(1);
        headerItems.add(headerItem);
        return addHeaderItems(headerItems);
    }

    public boolean addFooterItem(T footerItem) {
        if (footerItem == null) {
            Log.e(TAG, "No footerItem to add!");
            return false;
        }
        if (DEBUG) Log.v(TAG, "addItem delegates addition to addFooterItem!");
        List<T> footerItems = new ArrayList<>(1);
        footerItems.add(footerItem);
        return addFooterItems(footerItems);
    }

    /**
     * @param items items
     */
    public boolean addHeaderItems(List<T> items) {
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
    public boolean addFooterItems(List<T> items) {
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


    /**
     * @param item the item to check
     * @return true if the item implements {@link IExpandable} interface and its property has
     * {@code expanded = true}
     * @since 5.0.0-b1
     */
    public boolean isExpanded(@NonNull IFlexible item) {
        if (isExpandable(item)) {
            IExpandable expandable = (IExpandable) item;
            return expandable.isExpanded();
        }
        return false;
    }

    /**
     * @param position the position of the item to check
     * @return true if the item implements {@link IExpandable} interface and its property has
     * {@code expanded = true}
     * @since 5.0.0-b1
     */
    public boolean isExpanded(@IntRange(from = 0) int position) {
        return isExpanded(getItem(position));
    }

    private FlexibleAdapter setStickyHeaders(boolean headersSticky) {
        // Add or Remove the sticky headers
        if (headersSticky) {
            this.headersSticky = true;
            if (mStickyHeaderHelper == null)
                mStickyHeaderHelper = new StickyHeaderHelper(this, mStickyHeaderChangeListener);
            if (!mStickyHeaderHelper.isAttachedToRecyclerView())
                mStickyHeaderHelper.attachToRecyclerView(mRecyclerView);
        } else if (mStickyHeaderHelper != null) {
            this.headersSticky = false;
            mStickyHeaderHelper.detachFromRecyclerView(mRecyclerView);
            mStickyHeaderHelper = null;
        }
        return this;
    }
    public IHeader getSectionHeader(@IntRange(from = 0) int position) {
        //Headers are not visible nor sticky
        //When headers are visible and sticky, get the previous header
        for (int i = position; i >= 0; i--) {
            IFlexible item = getItem(i);
            if (isHeader(item)) return (IHeader) item;
        }
        return null;
    }
    public boolean hasSubItems(@NonNull IExpandable expandable) {
        return expandable != null && expandable.getSubItems() != null &&
                expandable.getSubItems().size() > 0;
    }

    private int expand(int position, boolean expandAll, boolean init) {
        IFlexible item = getItem(position);
        if (!isExpandable(item)) return 0;

        IExpandable expandable = (IExpandable) item;
        if (!hasSubItems(expandable)) {
            expandable.setExpanded(false);//clear the expanded flag
            if (DEBUG)
                Log.w(TAG, "No subItems to Expand on position " + position +
                        " expanded " + expandable.isExpanded());
            return 0;
        }
        if (DEBUG && !init) {
            Log.v(TAG, "Request to Expand on position=" + position +
                    " expanded=" + expandable.isExpanded() +
                    " ExpandedItems=" + getExpandedPositions());
        }
        int subItemsCount = 0;
        if (init || !expandable.isExpanded() && (expandable.getExpansionLevel() <= selectedLevel)) {

            //Collapse others expandable if configured so
            //Skip when expanding all is requested
            //Fetch again the new position after collapsing all!!
            if (collapseOnExpand && !expandAll && collapseAll(minCollapsibleLevel) > 0) {
                position = getGlobalPositionOf(item);
            }

            //Every time an expansion is requested, subItems must be taken from the
            // original Object and without the subItems marked hidden (removed)
            List<T> subItems = getExpandableList(expandable);
            mItems.addAll(position + 1, subItems);
            subItemsCount = subItems.size();
            //Save expanded state
            expandable.setExpanded(true);

            //Automatically smooth scroll the current expandable item to show as much
            // children as possible
            if (!init && scrollOnExpand && !expandAll) {
                autoScrollWithDelay(position, subItemsCount, 150L);
            }

            //Expand!
            notifyItemRangeInserted(position + 1, subItemsCount);
            //Show also the headers of the subItems

            if (DEBUG) {
                Log.v(TAG, (init ? "Initially expanded " : "Expanded ") +
                        subItemsCount + " subItems on position=" + position +
                        (init ? "" : " ExpandedItems=" + getExpandedPositions()));
            }
        }
        return subItemsCount;
    }
    private void autoScrollWithDelay(final int position, final int subItemsCount, final long delay) {
        //Must be delayed to give time at RecyclerView to recalculate positions after an automatic collapse
        new Handler(Looper.getMainLooper(), new Handler.Callback() {
            public boolean handleMessage(Message message) {
                int firstVisibleItem, lastVisibleItem;
                if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    firstVisibleItem = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPositions(null)[0];
                    lastVisibleItem = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPositions(null)[0];
                } else {
                    firstVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                }
                int itemsToShow = position + subItemsCount - lastVisibleItem;
                if (DEBUG)
                    Log.v(TAG, "autoScroll itemsToShow=" + itemsToShow + " firstVisibleItem=" + firstVisibleItem + " lastVisibleItem=" + lastVisibleItem + " RvChildCount=" + mRecyclerView.getChildCount());
                if (itemsToShow > 0) {
                    int scrollMax = position - firstVisibleItem;
                    int scrollMin = Math.max(0, position + subItemsCount - lastVisibleItem);
                    int scrollBy = Math.min(scrollMax, scrollMin);
                    int spanCount = getSpanCount(mRecyclerView.getLayoutManager());
                    if (spanCount > 1) {
                        scrollBy = scrollBy % spanCount + spanCount;
                    }
                    int scrollTo = firstVisibleItem + scrollBy;
                    if (DEBUG)
                        Log.v(TAG, "autoScroll scrollMin=" + scrollMin + " scrollMax=" + scrollMax + " scrollBy=" + scrollBy + " scrollTo=" + scrollTo);
                    mRecyclerView.smoothScrollToPosition(scrollTo);
                } else if (position < firstVisibleItem) {
                    mRecyclerView.smoothScrollToPosition(position);
                }
                return true;
            }
        }).sendMessageDelayed(null, delay);
    }

    public static int getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }

    public int expand(T item, boolean init) {
        return expand(getGlobalPositionOf(item), false, init);
    }

    public int expand(T item) {
        return expand(getGlobalPositionOf(item), false, false);
    }

    public int expand(@IntRange(from = 0) int position) {
        return expand(position, false, false);
    }

    @NonNull
    private List<T> getExpandableList(IExpandable expandable) {
        List<T> subItems = new ArrayList<T>();
        if (expandable != null && hasSubItems(expandable)) {
            List<T> allSubItems = expandable.getSubItems();
            for (T subItem : allSubItems) {
                //Pick up only no hidden items (doesn't get into account the filtered items)
                subItems.add(subItem);
            }
        }
        return subItems;
    }

    public int collapse(int position) {
        IFlexible item = getItem(position);
        if (!isExpandable(item)) return 0;

        IExpandable expandable = (IExpandable) item;
        if (DEBUG) {
            Log.v(TAG, "Request to Collapse on position=" + position +
                    " expanded=" + expandable.isExpanded() +
                    " ExpandedItems=" + getExpandedPositions());
        }
        int subItemsCount = 0, recursiveCount = 0;
        if (expandable.isExpanded()) {

            //Take the current subList
            List<T> subItems = getExpandableList(expandable);
            //Recursive collapse of all sub expandable
            recursiveCount = recursiveCollapse(subItems, expandable.getExpansionLevel());
            mItems.removeAll(subItems);
            subItemsCount = subItems.size();
            //Save expanded state
            expandable.setExpanded(false);

            //Collapse!
            notifyItemRangeRemoved(position + 1, subItemsCount);
            //Hide also the headers of the subItems


            if (DEBUG)
                Log.v(TAG, "Collapsed " + subItemsCount + " subItems on position " + position + " ExpandedItems=" + getExpandedPositions());
        }
        return subItemsCount + recursiveCount;
    }
    public int collapseAll(int level) {
        return recursiveCollapse(mItems, level);
    }
    private int recursiveCollapse(List<T> subItems, int level) {
        int collapsed = 0;
        for (int i = subItems.size() - 1; i >= 0; i--) {
            IFlexible subItem = subItems.get(i);
            if (isExpanded(subItem)) {
                IExpandable expandable = (IExpandable) subItem;
                if (expandable.getExpansionLevel() >= level &&
                        collapse(getGlobalPositionOf(subItem)) > 0) {
                    collapsed++;
                }
            }
        }
        return collapsed;
    }

    public List<Integer> getExpandedPositions() {
        List<Integer> expandedPositions = new ArrayList<Integer>();
        for (int i = 0; i < mItems.size() - 1; i++) {
            if (isExpanded(mItems.get(i)))
                expandedPositions.add(i);
        }
        return expandedPositions;
    }

    public ViewGroup getStickySectionHeadersHolder() {
        return (ViewGroup) ((Activity) mRecyclerView.getContext()).findViewById(R.id.sticky_header_container);
    }

    /**
     * @param item the item to check
     * @return true if the item implements {@link IExpandable} interface, false otherwise
     * @since 5.0.0-b1
     */
    public boolean isExpandable(@NonNull IFlexible item) {
        return item != null && item instanceof IExpandable;
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