package cc.easyandroid.easyrecyclerview.demo.text;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;
import cc.easyandroid.easyrecyclerview.demo.R;
import cc.easyandroid.easyrecyclerview.holders.FlexibleViewHolder;
import cc.easyandroid.easyrecyclerview.items.IHeader;
import cc.easyandroid.easyrecyclerview.items.IHeaderSpanFill;

/**
 * Created by cgpllx on 2016/9/9.
 */
public class MyHolder_sticky implements IHeader, IHeaderSpanFill {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(boolean hidden) {

    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void setSelectable(boolean selectable) {

    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return 1;
    }

    private int iiii = 0;

    public MyHolder_sticky(int i) {
        iiii = i;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_item_header;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(EasyFlexibleAdapter adapter, RecyclerView.ViewHolder viewHolder, int position, List payloads) {
//        get
//        adapter.get
        if (viewHolder instanceof MyHolder_sticky.ViewHolder) {
            ViewHolder viewHolder1 = (ViewHolder) viewHolder;
//            viewHolder1.mIdView.setText("item Header " + iiii+"--"+position);
//            viewHolder1.mContentView.setText(mValues);
        }
    }

    @Override
    public boolean isSticky() {
        return true;
    }

    public class ViewHolder extends FlexibleViewHolder {
//        public final TextView mIdView;
//        public final TextView mContentView;

        public ViewHolder(View view, EasyFlexibleAdapter adapter) {
            super(view, adapter, true);
//            mIdView = (TextView) view.findViewById(R.id.id);
//            mContentView = (TextView) view.findViewById(R.id.content);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
