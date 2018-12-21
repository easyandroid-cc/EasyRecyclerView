package cc.easyandroid.easyrecyclerview.items;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;

/**

 */
public abstract class AbstractEasyFlexibleItem<VH extends RecyclerView.ViewHolder>
        implements IFlexible<VH> {

    private static final String MAPPING_ILLEGAL_STATE = " is not implemented. If you want EasyFlexibleAdapter creates and binds ViewHolder for you, you must override and implement the method ";

    /* Item flags recognized by the FlexibleAdapter */
    protected boolean mEnabled = true, mSelectable = true;

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public int getLayoutRes() {
        return 0;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public boolean isSelectable() {
        return mSelectable;
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount;
    }

    @Override
    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if called but not implemented
     */
    @Override
    public VH createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        throw new IllegalStateException("onCreateViewHolder()" + MAPPING_ILLEGAL_STATE
                + this.getClass().getSimpleName() + ".createViewHolder().");
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if called but not implemented
     */
    @Override
    public void bindViewHolder(EasyFlexibleAdapter adapter, VH holder, int position, List payloads) {
        throw new IllegalStateException("onBindViewHolder()" + MAPPING_ILLEGAL_STATE
                + this.getClass().getSimpleName() + ".bindViewHolder().");
    }

}