package cc.easyandroid.easyrecyclerview.core.progress;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cc.easyandroid.easyrecyclerview.R;
import cc.easyandroid.easyrecyclerview.core.ProgressEmptyView;
import cc.easyandroid.easyrecyclerview.core.ProgressHander;
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

public class EasyProgressLinearLayout extends LinearLayout implements EasyProgressLayout {

    private ProgressHander mProgressHander;

    public EasyProgressLinearLayout(Context context) {
        this(context, null);
    }

    public EasyProgressLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EasyProgressLayoutStyle);
    }

    public EasyProgressLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ProgressEmptyView progressEmptyView = new ProgressEmptyView(context, attrs, defStyle);
        setProgressHander(progressEmptyView);
    }

    public void showLoadingView() {
        updata(false);
        mProgressHander.showLoadingView();
    }

    public void showEmptyView() {
        updata(false);
        mProgressHander.showEmptyView();
    }

    public void showErrorView() {
        updata(false);
        mProgressHander.showErrorView();
    }

    public void showContentView() {
        updata(true);
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

    void setProgressHander(ProgressHander progressHander) {
        mProgressHander = progressHander;
        setEmptyView(mProgressHander.getView());
    }

    private View emptyView;

    void updata(boolean hasData) {
        if (hasData && emptyView != null) {
            emptyView.setVisibility(View.GONE);
            EasyProgressLinearLayout.this.setVisibility(View.VISIBLE);
        } else {
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                EasyProgressLinearLayout.this.setVisibility(View.GONE);
            }
        }
    }

    void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}