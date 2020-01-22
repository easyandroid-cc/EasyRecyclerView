package cc.easyandroid.easyrecyclerview.demo;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.easyandroid.easyrecyclerview.EasyRecyclerAdapter;

public class MyAdapter extends EasyRecyclerAdapter<String> {

    public MyAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition) {
        if (viewHolder instanceof MyAdapter.ViewHolder) {
            MyAdapter.ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            viewHolder1.mIdView.setText("item "+RealPosition);
//            viewHolder1.mContentView.setText(mValues);
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
