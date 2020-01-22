package cc.easyandroid.easyrecyclerview.demo.text;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;
import cc.easyandroid.easyrecyclerview.demo.R;
import cc.easyandroid.easyrecyclerview.holders.ExpandableViewHolder;
import cc.easyandroid.easyrecyclerview.items.AbstractExpandableItem;
import cc.easyandroid.easyrecyclerview.items.IFlexible;
import cc.easyandroid.easyrecyclerview.items.IHeader;

/**
 * Created by cgpllx on 2016/9/9.
 */
public class MyHolder_Expand extends AbstractExpandableItem implements IHeader {


    public MyHolder_Expand() {
        List<IFlexible> items = new ArrayList<IFlexible>();
        for (int i = 0; i < 2; i++) {
            items.add(new MyHolder(i + 1200 + hashCode()));
        }
        items.add(new MyHolder_Expand2());
        setSubItems(items);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_item_expand;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(EasyFlexibleAdapter adapter, RecyclerView.ViewHolder viewHolder, int position, List payloads) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;


        return false;
    }

    @Override
    public boolean isSticky() {
        return true;
    }


    public class ViewHolder extends ExpandableViewHolder {

        public ViewHolder(View view, EasyFlexibleAdapter adapter) {
            super(view, adapter, true);
        }
    }
}
