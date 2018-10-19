package cc.easyandroid.easyrecyclerview.holders;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;
import cc.easyandroid.easyrecyclerview.SelectableAdapter;

/**
 * 一个抽象的holder，实现了一些简单的功能
 */
public abstract class FlexibleViewHolder extends ContentViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = FlexibleViewHolder.class.getSimpleName();

    //EasyFlexibleAdapter is needed to retrieve listeners and item status
    protected final EasyFlexibleAdapter mAdapter;


    public FlexibleViewHolder(View view, EasyFlexibleAdapter adapter) {
        this(view, adapter, false);
    }


    public FlexibleViewHolder(View view, EasyFlexibleAdapter adapter, boolean stickyHeader) {
        super(view, adapter, stickyHeader);
        this.mAdapter = adapter;

        getContentView().setOnClickListener(this);
        getContentView().setOnLongClickListener(this);
    }


    @Override
    @CallSuper
    public void onClick(View view) {
        final int position = getFlexibleAdapterPosition();
        if (!mAdapter.isEnabled(position)) return;
        //Experimented that, if LongClick is not consumed, onClick is fired. We skip the
        //call to the listener in this case, which is allowed only in ACTION_STATE_IDLE.
        if (mAdapter.mItemClickListener != null) {
            if (EasyFlexibleAdapter.DEBUG)
                Log.v(TAG, "onClick on position " + position);
            //Get the permission to activate the View from user
            if (position != RecyclerView.NO_POSITION) {
                toggleSelection(position);
                toggleActivation(position);
            }
            mAdapter.mItemClickListener.onItemClick(view,position);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0-b1
     */
    @Override
    @CallSuper
    public boolean onLongClick(View view) {
        final int position = getFlexibleAdapterPosition();
        if (!mAdapter.isEnabled(position)) return false;
        if (EasyFlexibleAdapter.DEBUG)
            Log.v(TAG, "onLongClick on position " + position);
        //If DragLongPress is enabled, then LongClick must be skipped and the listener will
        // be called in onActionStateChanged in Drag mode.
        if (mAdapter.mItemLongClickListener != null) {
            if (position != RecyclerView.NO_POSITION) {
                toggleSelection(position);
                toggleActivation(position);
            }
            mAdapter.mItemLongClickListener.onItemLongClick(view,position);
            return true;
        }
        return false;
    }

    @CallSuper
    protected void toggleActivation(int position) {
        boolean selected = mAdapter.isSelected(position);
        System.out.println("selected=" + selected + "  position=" + position);
 //       itemView.setActivated(selected);
    }

    public void toggleSelection(int position) {
        if (position >= 0 && (mAdapter.getMode() == SelectableAdapter.MODE_SINGLE ||
                mAdapter.getMode() == SelectableAdapter.MODE_MULTI)) {
            mAdapter.toggleSelection(position);
        }
    }

}