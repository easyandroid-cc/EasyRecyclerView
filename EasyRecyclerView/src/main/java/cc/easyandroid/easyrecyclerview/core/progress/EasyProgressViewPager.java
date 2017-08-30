package cc.easyandroid.easyrecyclerview.core.progress;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cc.easyandroid.easyrecyclerview.R;
import cc.easyandroid.easyrecyclerview.core.IEmptyAdapter;
import cc.easyandroid.easyrecyclerview.core.IProgressHander;
import cc.easyandroid.easyrecyclerview.core.ProgressEmptyView;
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

/**
 * EasyProgressViewPager
 */
public class EasyProgressViewPager extends ViewPager {
    public EasyProgressViewPager(Context context) {
        this(context, null);
    }

    public EasyProgressViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        ProgressEmptyView progressEmptyView = new ProgressEmptyView(this, attrs, R.attr.EasyRecyclerViewStyle);
        setProgressHander(progressEmptyView);
    }

    public void setProgressHander(IProgressHander progressHander) {
        mProgressHander = progressHander;
        setEmptyView(progressHander.getView());
    }

    private IProgressHander mProgressHander;


    public void setAdapter(PagerAdapter adapter) {
        PagerAdapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(emptyObserver);
        }
        if (adapter != null) {
            adapter.registerDataSetObserver(emptyObserver);
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

    private DataSetObserver emptyObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updata();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            updata();
        }
    };

    void updata() {
        PagerAdapter adapter = getAdapter();
        if (adapter != null) {
            if (adapter instanceof IEmptyAdapter) {
                IEmptyAdapter iEmptyAdapter = (IEmptyAdapter) adapter;
                if (!iEmptyAdapter.isEmpty() && emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                    EasyProgressViewPager.this.setVisibility(View.VISIBLE);
                    return;
                }
            }

        }
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
            EasyProgressViewPager.this.setVisibility(View.GONE);
        }

    }


    void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

}
