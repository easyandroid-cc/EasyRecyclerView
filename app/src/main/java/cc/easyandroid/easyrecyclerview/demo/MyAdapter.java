package cc.easyandroid.easyrecyclerview.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyRecyclerAdapter;
import cc.easyandroid.easyrecyclerview.demo.dummy.DummyContent.DummyItem;

public class MyAdapter extends EasyRecyclerAdapter<DummyItem> {

    public MyAdapter(List<DummyItem> items) {
//        addDatas(items);
    }
    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, DummyItem mValues) {
        if (viewHolder instanceof MyAdapter.ViewHolder) {
            MyAdapter.ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            viewHolder1.mIdView.setText(mValues.id);
            viewHolder1.mContentView.setText(mValues.content);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
