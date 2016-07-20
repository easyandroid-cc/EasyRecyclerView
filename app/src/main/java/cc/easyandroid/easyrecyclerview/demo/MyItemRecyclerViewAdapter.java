package cc.easyandroid.easyrecyclerview.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyRecyclerAdapter;
import cc.easyandroid.easyrecyclerview.demo.ItemFragment.OnListFragmentInteractionListener;
import cc.easyandroid.easyrecyclerview.demo.dummy.DummyContent.DummyItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends EasyRecyclerAdapter<DummyItem> {
//    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
      addDatas(items);
        mListener = listener;
    }
    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, DummyItem mValues) {
        if(viewHolder instanceof MyItemRecyclerViewAdapter.ViewHolder){
            MyItemRecyclerViewAdapter.ViewHolder viewHolder1= (ViewHolder) viewHolder;
            viewHolder1.mIdView.setText(mValues.id);
            viewHolder1.mContentView.setText(mValues.content);
        }
    }

//    @Override
//    public void onBind(MyItemRecyclerViewAdapter.ViewHolder viewHolder, int RealPosition, DummyItem mValues) {
////        viewHolder.mItem = mValues.get(position);
//        viewHolder.mIdView.setText(mValues.id);
//        viewHolder.mContentView.setText(mValues.content);
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
