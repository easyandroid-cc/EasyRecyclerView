package cc.easyandroid.easyrecyclerview.demo.text;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.easyandroid.easyrecyclerview.demo.R;

/**
 * Created by cgpllx on 2016/9/9.
 */
public class MyHolder implements IFlexible {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    private int iiii = 0;

    public MyHolder(int i) {
        iiii = i;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_item;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false),adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, RecyclerView.ViewHolder viewHolder, int position, List payloads) {
//        get
//        adapter.get
        if (viewHolder instanceof MyHolder.ViewHolder) {
            ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            viewHolder1.mIdView.setText("item " + iiii+"--"+position);
//            viewHolder1.mContentView.setText(mValues);
        }
    }

    public class ViewHolder extends FlexibleViewHolder  {
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view,FlexibleAdapter adapter) {
            super(view,adapter);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
