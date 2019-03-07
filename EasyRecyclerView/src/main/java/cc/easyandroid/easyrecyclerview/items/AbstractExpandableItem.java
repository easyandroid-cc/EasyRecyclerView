package cc.easyandroid.easyrecyclerview.items;

import java.util.ArrayList;
import java.util.List;

import cc.easyandroid.easyrecyclerview.holders.ExpandableViewHolder;


public abstract class AbstractExpandableItem<VH extends ExpandableViewHolder, S extends IFlexible>
        extends AbstractEasyFlexibleItem<VH>
        implements IExpandable<VH, S> {

    /* Flags for FlexibleAdapter */
    protected boolean mExpanded = false;

    /* subItems list */
    protected List<S> mSubItems;

	/*--------------------*/
    /* EXPANDABLE METHODS */
	/*--------------------*/

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.mExpanded = expanded;
    }


	/*-------------------*/
	/* SUB ITEMS METHODS */
	/*-------------------*/

    @Override
    public final List<S> getSubItems() {
        return mSubItems;
    }

    public final boolean hasSubItems() {
        return mSubItems != null && mSubItems.size() > 0;
    }

    public AbstractExpandableItem setSubItems(List<S> subItems) {
        mSubItems = subItems;
        return this;
    }

    public AbstractExpandableItem addSubItems(int position, List<S> subItems) {
        if (mSubItems != null && position >= 0 && position < mSubItems.size()) {
            mSubItems.addAll(position, subItems);
        } else {
            if (mSubItems == null)
                mSubItems = new ArrayList<>();
            mSubItems.addAll(subItems);
        }
        return this;
    }

    public final int getSubItemsCount() {
        return mSubItems != null ? mSubItems.size() : 0;
    }

    public S getSubItem(int position) {
        if (mSubItems != null && position >= 0 && position < mSubItems.size()) {
            return mSubItems.get(position);
        }
        return null;
    }

    public final int getSubItemPosition(S subItem) {
        return mSubItems != null ? mSubItems.indexOf(subItem) : -1;
    }

    public AbstractExpandableItem addSubItem(S subItem) {
        if (mSubItems == null)
            mSubItems = new ArrayList<>();
        mSubItems.add(subItem);
        return this;
    }

    public AbstractExpandableItem addSubItem(int position, S subItem) {
        if (mSubItems != null && position >= 0 && position < mSubItems.size()) {
            mSubItems.add(position, subItem);
        } else {
            addSubItem(subItem);
        }
        return this;
    }

    public boolean contains(S subItem) {
        return mSubItems != null && mSubItems.contains(subItem);
    }

    public boolean removeSubItem(S item) {
        return item != null && mSubItems != null && mSubItems.remove(item);
    }

    public boolean removeSubItems(List<S> subItems) {
        return subItems != null && mSubItems != null && mSubItems.removeAll(subItems);
    }

    public boolean removeSubItem(int position) {
        if (mSubItems != null && position >= 0 && position < mSubItems.size()) {
            mSubItems.remove(position);
            return true;
        }
        return false;
    }

}