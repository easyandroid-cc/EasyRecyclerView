package cc.easyandroid.easyrecyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cgp
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_HEADER = 1 << 24;//2 24 header item
    public static final int TYPE_FOOTER = 1 << 25;//2 25 foot item

    private ArrayList<T> mDatas = new ArrayList<>();
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();


    private OnItemClickListener<T> mListener;

    public void setOnItemClickListener(OnItemClickListener<T> li) {
        mListener = li;
    }

    public void addHeaderView(View headerView) {
        mHeaderViews.add(headerView);
        notifyItemInserted(mHeaderViews.size() - 1);
    }

    public void addFooterView(View footerView) {
        mFooterViews.add(footerView);
        notifyItemInserted(getItemCount() - 1);//
    }

    public void addDatas(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaderViews.size()) {
            return TYPE_HEADER | position;
        } else if (position >= (mHeaderViews.size() + mDatas.size())) {
            return TYPE_FOOTER | (position - (mHeaderViews.size() + mDatas.size()));
        }
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if ((viewType & TYPE_HEADER) == TYPE_HEADER) {
            return new Holder(mHeaderViews.get((viewType ^ TYPE_HEADER)));//header
        } else if ((viewType & TYPE_FOOTER) == TYPE_FOOTER) {
            return new Holder(mFooterViews.get((viewType ^ TYPE_FOOTER)));// footer
        }
        return onCreate(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if ((getItemViewType(position) & TYPE_HEADER) == TYPE_HEADER) return;//header
        if (position >= (mHeaderViews.size() + mDatas.size())) {//
            if ((getItemViewType(position) & TYPE_FOOTER) == TYPE_FOOTER)
                return;//footer
        }
        final int pos = getRealPosition(viewHolder);
        final T data = mDatas.get(pos);
        onBind(viewHolder, pos, data);

        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("EasyRecyclerView setOnClickListener");
                    mListener.onItemClick(pos, data);
                }
            });
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((getItemViewType(position) & TYPE_HEADER) == TYPE_HEADER)//
                            ||((getItemViewType(position) & TYPE_FOOTER) == TYPE_FOOTER)//
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return position - mHeaderViews.size();
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + mHeaderViews.size() + mFooterViews.size();
    }

    public int getNormalItemCount() {
        return mDatas.size();
    }

    public void clear(){
        mDatas.clear();
    }

    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, final int viewType);

    public abstract void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, T data);

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }

}
