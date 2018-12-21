package cc.easyandroid.easyrecyclerview.items;

import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;

/**
 * 自定义的ViewHolder
 *
 * @param <VH>
 */
public interface IFlexible<VH extends RecyclerView.ViewHolder> {

    /**
     * Returns if the Item is enabled.
     *
     * @return (default) true for enabled item, false for disabled one.
     */
    boolean isEnabled();

    /**
     * Setter to change enabled behaviour.
     *
     * @param enabled false to disable all operations on this item
     */
    void setEnabled(boolean enabled);

    /**
     * Returns if the item can be selected.
     *
     * @return (default) true for a Selectable item, false otherwise
     */
    boolean isSelectable();

    /**
     * Setter to change selectable behaviour.
     *
     * @param selectable false to disable selection on this item
     */
    void setSelectable(boolean selectable);

    @IntRange(from = 1)
    int getSpanSize(int spanCount, int position);

    @LayoutRes
    int getLayoutRes();


    VH createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent);


    void bindViewHolder(EasyFlexibleAdapter adapter, VH holder, int position, List payloads);

}