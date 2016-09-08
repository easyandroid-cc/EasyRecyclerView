package cc.easyandroid.easyrecyclerview.demo.text;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlexibleAdapter<T extends IFlexible> extends RecyclerView.Adapter {
    public static boolean DEBUG = true;

    private static final String TAG = FlexibleAdapter.class.getSimpleName();

    protected RecyclerView mRecyclerView;
    /**
     * The main container for ALL items.
     */
    private List<T> mItems;
    private List<T> mHeaderItems;
    private List<T> mFooterItems;


    /* ViewTypes */
    protected LayoutInflater mInflater;

    private SparseArray<T> mTypeInstances = new SparseArray<>();

    private boolean autoMap = false;

    public OnItemClickListener mItemClickListener;
    public OnItemLongClickListener mItemLongClickListener;

    public FlexibleAdapter(@NonNull List<T> items) {
        this(items, null);
    }


    public FlexibleAdapter(@NonNull List<T> items, @Nullable Object listeners) {
        mItems = Collections.synchronizedList(items);

        //Create listeners instances
        initializeListeners(listeners);
        //Get notified when items are inserted or removed (it adjusts selected positions)
    }


    public FlexibleAdapter initializeListeners(@Nullable Object listeners) {
        if (listeners instanceof OnItemClickListener)
            mItemClickListener = (OnItemClickListener) listeners;
        if (listeners instanceof OnItemLongClickListener)
            mItemLongClickListener = (OnItemLongClickListener) listeners;
        return this;
    }

    public final T getItem(@IntRange(from = 0) int position) {
        if (position < 0 || position >= mItems.size()) return null;
        return mItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    @Override
    public final int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }


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
        return getItemCount() == 0;
    }

    public int getGlobalPositionOf(@NonNull IFlexible item) {
        return item != null && mItems != null && !mItems.isEmpty() ? mItems.indexOf(item) : -1;
    }


    @NonNull
    public List<IHeader> getHeaderItems() {
        List<IHeader> headers = new ArrayList<IHeader>();
        for (T item : mItems) {
            if (isHeader(item))
                headers.add((IHeader) item);
        }
        return headers;
    }

    public boolean isHeader(T item) {
        return item != null && item instanceof IHeader;
    }

    /**
     * layout id就是item的type
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        T item = getItem(position);
        //Map the view type if not done yet
        mapViewTypeFrom(item);
        autoMap = true;
        return item.getLayoutRes();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        T item = getViewTypeInstance(viewType);
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
        T item = getItem(position);
        if (item != null) {
            item.bindViewHolder(this, holder, position, payloads);
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
        mItems.set(position, item);
        if (DEBUG) Log.v(TAG, "updateItem notifyItemChanged on position " + position);
        notifyItemChanged(position, payload);
    }


    public boolean addItem(@IntRange(from = 0) int position, @NonNull T item) {
        if (item == null) {
            Log.e(TAG, "No items to add!");
            return false;
        }
        if (DEBUG) Log.v(TAG, "addItem delegates addition to addItems!");
        List<T> items = new ArrayList<T>(1);
        items.add(item);
        return addItems(position, items);
    }


    public boolean addItems(@IntRange(from = 0) int position, @NonNull List<T> items) {
        if (position < 0) {
            Log.e(TAG, "Cannot addItems on negative position!");
            return false;
        }
        if (items == null || items.isEmpty()) {
            Log.e(TAG, "No items to add!");
            return false;
        }
        if (DEBUG) Log.v(TAG, "addItems on position=" + position + " itemCount=" + items.size());

        //Insert Items
        if (position < mItems.size()) {
            mItems.addAll(position, items);
        } else {
            mItems.addAll(items);
        }
        //Notify range addition
        notifyItemRangeInserted(position, items.size());

        return true;
    }


    /**
     * 讲type和 t 映射mTypeInstancesz中
     *
     * @param item
     */
    private void mapViewTypeFrom(T item) {

        if (item != null && !(mTypeInstances.indexOfKey(item.getLayoutRes()) < 0)) {
            mTypeInstances.put(item.getLayoutRes(), item);
            if (DEBUG)
                Log.i(TAG, "Mapped viewType " + item.getLayoutRes() + " from " + item.getClass().getSimpleName());
        }
    }

    public boolean isEnabled(int position) {
        T item = getItem(position);
        return item != null && item.isEnabled();
    }

    /**
     * 根据view type 获取对应的对象
     *
     * @param viewType
     * @return
     */
    private T getViewTypeInstance(int viewType) {
        return mTypeInstances.get(viewType);
    }


    public interface OnItemClickListener {

        boolean onItemClick(int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(int position);
    }

}