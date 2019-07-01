package cc.easyandroid.easyrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cc.easyandroid.easyrecyclerview.core.IEmptyAdapter;
import cc.easyandroid.easyrecyclerview.core.IProgressHander;
import cc.easyandroid.easyrecyclerview.core.ProgressEmptyView;
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

/**
 * EasyProgressRecyclerView
 */
public class EasyProgressRecyclerView extends RecyclerView {

    public EasyProgressRecyclerView(Context context) {
        this(context, null);
    }

    public EasyProgressRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EasyRecyclerViewStyle);
    }

    public EasyProgressRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ProgressEmptyView progressEmptyView = new ProgressEmptyView(this, attrs, defStyle);
        setProgressHander(progressEmptyView);

    }

    public void setProgressHander(IProgressHander progressHander) {
        mProgressHander = progressHander;
        setEmptyView(progressHander.getView());
    }

    private IProgressHander mProgressHander;

    public void setAdapter(Adapter adapter) {

        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(emptyObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }
        super.setAdapter(adapter);
        emptyObserver.onChanged();
    }


    public void showLoadingView() {
        showLoadingView(null);
    }

    public void showEmptyView() {
        showEmptyView(null);
    }

    public void showErrorView() {
        showErrorView(null);
    }

    public void showLoadingView(String message) {
        mProgressHander.showLoadingView(message);
    }

    public void showEmptyView(String message) {
        mProgressHander.showEmptyView(message);
    }

    public void showErrorView(String message) {
        mProgressHander.showErrorView(message);
    }

    public void setOnEasyProgressClickListener(OnEasyProgressClickListener listener) {
        mProgressHander.setOnEasyProgressClickListener(listener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 要添加在window后才能找到parent
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " is not attached to parent view.");
        }
        if (emptyView != null) {
            parent.removeView(emptyView);
            parent.addView(emptyView);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private View emptyView;

    public View getEmptyView() {
        return emptyView;
    }

    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        public void onItemRangeChanged(int positionStart, int itemCount) {
            updata();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            updata();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updata();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            updata();
        }

        @Override
        public void onChanged() {
            updata();
        }
    };

    void updata() {
        Adapter<?> adapter = getAdapter();
        if (adapter != null) {
            if (adapter instanceof IEmptyAdapter) {
                IEmptyAdapter iEmptyAdapter = (IEmptyAdapter) adapter;
                if (!iEmptyAdapter.isEmpty() && emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                    EasyProgressRecyclerView.this.setVisibility(View.VISIBLE);
                    return;
                }
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                EasyProgressRecyclerView.this.setVisibility(View.GONE);
            }
        }
    }


    void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

}
