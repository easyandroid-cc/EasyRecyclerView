package cc.easyandroid.easyrecyclerview.holders;

import androidx.annotation.CallSuper;
import android.view.View;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;


public abstract class ExpandableViewHolder extends FlexibleViewHolder {


    public ExpandableViewHolder(View view, EasyFlexibleAdapter adapter) {
        super(view, adapter);
    }


    public ExpandableViewHolder(View view, EasyFlexibleAdapter adapter, boolean stickyHeader) {
        super(view, adapter, stickyHeader);
    }


    protected boolean isViewExpandableOnClick() {
        return true;
    }


    protected boolean isViewCollapsibleOnClick() {
        return true;
    }


    protected boolean isViewCollapsibleOnLongClick() {
        return true;
    }


    protected boolean shouldNotifyParentOnClick() {
        return false;
    }


    protected void toggleExpansion() {
        int position = getFlexibleAdapterPosition();
        if (isViewCollapsibleOnClick() && mAdapter.isExpanded(position)) {
            collapseView(position);
        } else if (isViewExpandableOnClick() && !mAdapter.isSelected(position)) {
            expandView(position);
        }
    }


    protected void expandView(int position) {
        mAdapter.expand(position, shouldNotifyParentOnClick());
    }


    protected void collapseView(int position) {
        mAdapter.collapse(position, shouldNotifyParentOnClick());
        // #320 - Sticky header is not shown correctly once collapsed
        // Scroll to this position if this Expandable is currently sticky
        if (itemView.getX() < 0 || itemView.getY() < 0) {
             mAdapter.getRecyclerView().scrollToPosition(position);
        }
    }

	/*---------------------------------*/
	/* CUSTOM LISTENERS IMPLEMENTATION */
	/*---------------------------------*/

    /**
     * Called when user taps once on the ItemView.
     * <p><b>Note:</b> In Expandable version, it tries to expand, but before,
     * it checks if the view {@link #isViewExpandableOnClick()}.</p>
     *
     * @param view the view that receives the event
     */
    @Override
    @CallSuper
    public void onClick(View view) {
        if (mAdapter.isItemEnabled(getFlexibleAdapterPosition())) {
            toggleExpansion();
        }
        super.onClick(view);
    }

    /**
     * Called when user long taps on this itemView.
     * <p><b>Note:</b> In Expandable version, it tries to collapse, but before,
     * it checks if the view {@link #isViewCollapsibleOnLongClick()}.</p>
     *
     * @param view the view that receives the event
     */
    @Override
    @CallSuper
    public boolean onLongClick(View view) {
        int position = getFlexibleAdapterPosition();
        if (mAdapter.isItemEnabled(position) && isViewCollapsibleOnLongClick()) {
            collapseView(position);
        }
        return super.onLongClick(view);
    }

}