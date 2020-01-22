package cc.easyandroid.easyrecyclerview.items;

import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
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
     * (Internal usage).
     * When and item has been deleted (with Undo) or has been filtered out by the
     * adapter, then, it has hidden status.
     *
     * @return true for hidden item, (default) false for the shown one.
     */
    boolean isHidden();

    /**
     * Setter to change hidden behaviour. Useful while filtering this item.
     * Default value is false.
     *
     * @param hidden true if this item should remain hidden, false otherwise
     */
    void setHidden(boolean hidden);
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

    /**
     *  Individual item's span size to use only with {@code GridLayoutManager}.
     * @param spanCount
     * @param position
     * @return
     */
    @IntRange(from = 1)
    int getSpanSize(int spanCount, int position);

    @LayoutRes
    int getLayoutRes();


    VH createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent);


    void bindViewHolder(EasyFlexibleAdapter adapter, VH holder, int position, List payloads);

}