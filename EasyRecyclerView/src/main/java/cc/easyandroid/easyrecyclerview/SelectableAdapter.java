package cc.easyandroid.easyrecyclerview;

import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cc.easyandroid.easyrecyclerview.core.IStateAdapter;
import cc.easyandroid.easyrecyclerview.holders.FlexibleViewHolder;

/**
 * This class provides a set of standard methods to handle the selection on the items of an Adapter.
 */
public abstract class SelectableAdapter extends RecyclerView.Adapter implements IStateAdapter {

    private static final String TAG = SelectableAdapter.class.getSimpleName();
    public static boolean DEBUG = true;

    /**
     * Adapter will not keep track of selections
     */
    public static final int MODE_IDLE = 0;
    /**
     * Default mode for selection
     */
    public static final int MODE_SINGLE = 1;
    /**
     * Multi selection will be activated
     */
    public static final int MODE_MULTI = 2;

    /**
     * Annotation interface for selection modes.
     */
    @IntDef({MODE_IDLE, MODE_SINGLE, MODE_MULTI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    private final Set<Integer> mSelectedPositions;
    private final Set<FlexibleViewHolder> mBoundViewHolders;
    private int mMode;
    protected RecyclerView mRecyclerView;


	/*--------------*/
    /* CONSTRUCTORS */
    /*--------------*/

    /**
     */
    public SelectableAdapter() {
        mSelectedPositions = new TreeSet<Integer>();
        mBoundViewHolders = new HashSet<>();
        mMode = MODE_IDLE;
    }

	/*----------------*/
    /* STATIC METHODS */
    /*----------------*/

    /**
     * Call this once, to enable or disable DEBUG logs.
     * DEBUG logs are disabled by default.
     *
     * @param enable true to show DEBUG logs in verbose mode, false to hide them.
     */
    public static void enableLogs(boolean enable) {
        DEBUG = enable;
    }

	/*--------------*/
    /* MAIN METHODS */
    /*--------------*/

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    /**
     * @return the RecyclerView instance
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * Helper method to return the number of the columns (span count) of the given LayoutManager.
     * <p>All Layouts are supported.</p>
     *
     * @param layoutManager the layout manager to check
     * @return the span count
     * @since 5.0.0-b7
     */
    public static int getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }

    /**
     * Sets the mode of the selection:
     * <ul>
     * <li>{@link #MODE_IDLE} Default. Configures the adapter so that no item can be selected;
     * <li>{@link #MODE_SINGLE} configures the adapter to react at the single tap over an item
     * (previous selection is cleared automatically);
     * <li>{@link #MODE_MULTI} configures the adapter to save the position to the list of the
     * selected items.
     * </ul>
     *
     * @param mode one of {@link #MODE_IDLE}, {@link #MODE_SINGLE}, {@link #MODE_MULTI}
     */
    public void setMode(@Mode int mode) {
        if (mMode == MODE_SINGLE && mode == MODE_IDLE)
            clearSelection();
        this.mMode = mode;
    }

    /**
     * The current selection mode of the Adapter.
     *
     * @return current mode
     * @see #MODE_IDLE
     * @see #MODE_SINGLE
     * @see #MODE_MULTI
     * @since 2.1.0
     */
    @Mode
    public int getMode() {
        return mMode;
    }

    /**
     * Indicates if the item, at the provided position, is selected.
     *
     * @param position Position of the item to check.
     * @return true if the item is selected, false otherwise.
     */
    public boolean isSelected(int position) {
        return mSelectedPositions.contains(position);
    }

    /**
     * Checks if the current item has the property {@code selectable = true}.
     *
     * @param position the current position of the item to check
     * @return true if the item property selectable is true, false otherwise
     */
    public abstract boolean isSelectable(int position);

    /**
     * @param position Position of the item to toggle the selection status for.
     */
    public void toggleSelection(int position) {
        if (position < 0) return;
        if (mMode == MODE_SINGLE) {
//            mSelectedPositions.clear();
            clearSelection();
        }

        boolean contains = mSelectedPositions.contains(position);
        if (contains) {//如果已经被选择了
            removeSelection(position);//从mSelectedPositions删除position
        } else {
            addSelection(position);//把当前的位置添加到mSelectedPositions中，然后itemview会根据是否select 做去相应的操作
        }
        if (DEBUG) Log.v(TAG, "toggleSelection " + (contains ? "removed" : "added") +
                " on position " + position + ", current " + mSelectedPositions);
    }

    /**
     * Adds the selection status for the given position without notifying the change.
     *
     * @param position Position of the item to add the selection status for.
     * @return true if the set is modified, false otherwise or position is not currently selectable
     * @see #isSelectable(int)
     */
    public boolean addSelection(int position) {
        return isSelectable(position) && mSelectedPositions.add(position);
    }

    /**
     * Removes the selection status for the given position without notifying the change.
     *
     * @param position Position of the item to remove the selection status for.
     * @return true if the set is modified, false otherwise
     */
    public boolean removeSelection(int position) {
        return mSelectedPositions.remove(position);
    }

    /**
     * Clears the selection status for all items one by one and it doesn't stop animations in the items.
     * <b>Note 1:</b> Items are invalidated and rebound!
     * <b>Note 2:</b> This method use java.util.Iterator to avoid java.util.ConcurrentModificationException.
     */
    public void clearSelection() {
        if (DEBUG) Log.v(TAG, "clearSelection " + mSelectedPositions);
        Iterator<Integer> iterator = mSelectedPositions.iterator();
        int positionStart = 0, itemCount = 0;
        //The notification is done only on items that are currently selected.
        while (iterator.hasNext()) {
            int position = iterator.next();
            iterator.remove();
            //Optimization for ItemRangeChanged
            if (positionStart + itemCount == position) {
                itemCount++;
            } else {//多个
                //Notify previous items in range
                notifySelectionChanged(positionStart, itemCount);
                positionStart = position;
                itemCount = 1;
            }
        }
        //Notify remaining items in range
        notifySelectionChanged(positionStart, itemCount);
    }

    private void notifySelectionChanged(int positionStart, int itemCount) {
        if (itemCount > 0) notifyItemRangeChanged(positionStart, itemCount);
    }

    /**
     * Perform a quick, in-place update of the checked or activated state
     * on all visible item views. This should only be called when a valid
     * choice mode is active.
     */
    void updateOnScreenCheckedViews() {

        final int count = getRecyclerView().getChildCount();
        final boolean useActivated = getRecyclerView().getContext().getApplicationInfo().targetSdkVersion
                >= android.os.Build.VERSION_CODES.HONEYCOMB;
        for (int i = 0; i < count; i++) {
            final View child = getRecyclerView().getChildAt(i);
            final int position = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();

            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(isSelected(position));
            } else if (useActivated) {
                child.setActivated(isSelected(position));
            }
        }
    }

    public void clearChoices() {
        mSelectedPositions.clear();
        updateOnScreenCheckedViews();
    }

    public void setItemChecked(int position, boolean checked) {
        if (mMode == MODE_SINGLE) {
            mSelectedPositions.clear();
        }
        boolean selected = isSelected(position);
        if (checked && !selected) {
            addSelection(position);
        } else if (selected) {
            removeSelection(position);
        }
        updateOnScreenCheckedViews();
    }

    /**
     * Counts the selected items.
     *
     * @return Selected items count
     */
    public int getSelectedItemCount() {
        return mSelectedPositions.size();
    }

    /**
     * Retrieves the list of selected items.
     * <p>The list is a copy and it's sorted.</p>
     *
     * @return A copied List of selected items ids from the Set
     */
    public List<Integer> getSelectedPositions() {
        return new ArrayList<Integer>(mSelectedPositions);
    }


    /**
     * Saves  mSelectedPositions.
     *
     * @param outState Current state
     */

    public void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(TAG, new ArrayList<Integer>(mSelectedPositions));
    }

    /**
     * 恢复mSelectedPositions
     *
     * @param savedInstanceState Previous state
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mSelectedPositions.addAll(savedInstanceState.getIntegerArrayList(TAG));
        Log.d(TAG, "restore selection " + mSelectedPositions);
    }

}