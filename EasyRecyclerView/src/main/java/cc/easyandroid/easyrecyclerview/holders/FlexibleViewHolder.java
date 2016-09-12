package cc.easyandroid.easyrecyclerview.holders;

import android.support.annotation.CallSuper;
import android.util.Log;
import android.view.View;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;

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
        int position = getFlexibleAdapterPosition();
        if (!mAdapter.isEnabled(position)) return;
        //Experimented that, if LongClick is not consumed, onClick is fired. We skip the
        //call to the listener in this case, which is allowed only in ACTION_STATE_IDLE.
        if (mAdapter.mItemClickListener != null) {
            if (EasyFlexibleAdapter.DEBUG)
                Log.v(TAG, "onClick on position " + position);
            //Get the permission to activate the View from user
            mAdapter.mItemClickListener.onItemClick(position);


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
        int position = getFlexibleAdapterPosition();
        if (!mAdapter.isEnabled(position)) return false;
        if (EasyFlexibleAdapter.DEBUG)
            Log.v(TAG, "onLongClick on position " + position);
        //If DragLongPress is enabled, then LongClick must be skipped and the listener will
        // be called in onActionStateChanged in Drag mode.
        if (mAdapter.mItemLongClickListener != null) {
            mAdapter.mItemLongClickListener.onItemLongClick(position);
            return true;
        }
        return false;
    }


}