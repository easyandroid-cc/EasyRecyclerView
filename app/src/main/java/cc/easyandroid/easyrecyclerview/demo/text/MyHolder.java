package cc.easyandroid.easyrecyclerview.demo.text;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;
import cc.easyandroid.easyrecyclerview.demo.R;
import cc.easyandroid.easyrecyclerview.holders.FlexibleViewHolder;
import cc.easyandroid.easyrecyclerview.items.IFlexible;

/**
 * Created by cgpllx on 2016/9/9.
 */
public class MyHolder implements IFlexible<MyHolder.ViewHolder> {
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
        return true;
    }

    @Override
    public void setSelectable(boolean selectable) {

    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return 1;
    }

    private int iiii = 0;

    public MyHolder(int i) {
        iiii = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyHolder myHolder = (MyHolder) o;

        return iiii == myHolder.iiii;
    }

    @Override
    public int hashCode() {
        return iiii;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_item;
    }

    @Override
    public MyHolder.ViewHolder createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false),adapter);
    }

    @Override
    public void bindViewHolder(EasyFlexibleAdapter adapter, MyHolder.ViewHolder viewHolder, int position, List payloads) {
//        get
        viewHolder.setData(this);
//        adapter.get
        if (viewHolder instanceof MyHolder.ViewHolder) {
            ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            viewHolder1.mIdView.setText("item " + iiii+"--"+position);
//            viewHolder1.mContentView.setText(mValues);
        }
    }

    public class ViewHolder extends FlexibleViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view,EasyFlexibleAdapter adapter) {
            super(view,adapter);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
        MyHolder holder;
        public void setData(MyHolder holder){
            this.holder=holder;
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
        Toast  toast;
        @Override
        public void onClick(View view) {
            super.onClick(view);

            int count = mAdapter.getGlobalPositionOf(holder);
//            int count = mAdapter.getGlobalPositionOf(MyHolder.this);
            System.out.println("cgp count="+count);
           // mAdapter.removeItem(MyHolder.this);
            if (toast != null) {
                toast.setText( getAdapterPosition() + "");
            } else {
                toast = Toast.makeText(view.getContext(),  getAdapterPosition() + "", Toast.LENGTH_SHORT);
            }
//            toast.show();

        }
    }
}
