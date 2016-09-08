package cc.easyandroid.easyrecyclerview.demo.text;

import android.support.annotation.CallSuper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

public abstract class FlexibleViewHolder extends ContentViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = FlexibleViewHolder.class.getSimpleName();

    //FlexibleAdapter is needed to retrieve listeners and item status
    protected final FlexibleAdapter mAdapter;

    //These 2 fields avoid double tactile feedback triggered by Android during the touch event
    // (Drag or Swipe), also assure the LongClick event is correctly fired for ActionMode if that
    // was the user intention.

    //State for Dragging & Swiping actions
    protected int mActionState = ItemTouchHelper.ACTION_STATE_IDLE;


    /**
     * Default constructor.
     *
     * @param view    The {@link View} being hosted in this ViewHolder
     * @param adapter Adapter instance of type {@link FlexibleAdapter}
     * @since 5.0.0-b1
     */
    public FlexibleViewHolder(View view, FlexibleAdapter adapter) {
        this(view, adapter, false);
    }

    /**
     * Constructor to configure the sticky behaviour of a view.
     * <p><b>Note:</b> StickyHeader works only if the item has been declared of type
     *
     * @param view         The {@link View} being hosted in this ViewHolder
     * @param adapter      Adapter instance of type {@link FlexibleAdapter}
     * @param stickyHeader true if the View can be a Sticky Header, false otherwise
     * @since 5.0.0-b7
     */
    public FlexibleViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
        super(view, adapter, stickyHeader);
        this.mAdapter = adapter;

        getContentView().setOnClickListener(this);
        getContentView().setOnLongClickListener(this);
    }

	/*--------------------------------*/
    /* CLICK LISTENERS IMPLEMENTATION */
    /*--------------------------------*/

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0-b1
     */
    @Override
    @CallSuper
    public void onClick(View view) {
        int position = getFlexibleAdapterPosition();
        if (!mAdapter.isEnabled(position)) return;
        //Experimented that, if LongClick is not consumed, onClick is fired. We skip the
        //call to the listener in this case, which is allowed only in ACTION_STATE_IDLE.
        if (mAdapter.mItemClickListener != null && mActionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (FlexibleAdapter.DEBUG)
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
        if (FlexibleAdapter.DEBUG)
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