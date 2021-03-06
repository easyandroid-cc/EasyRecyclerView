package cc.easyandroid.easyrecyclerview.core.progress;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cc.easyandroid.easyrecyclerview.R;
import cc.easyandroid.easyrecyclerview.core.ProgressEmptyView;
import cc.easyandroid.easyrecyclerview.core.IProgressHander;
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

public class EasyProgressFrameLayout extends FrameLayout implements EasyProgressLayout {

    private IProgressHander mProgressHander;

    public EasyProgressFrameLayout(Context context) {
        this(context, null);
    }

    public EasyProgressFrameLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EasyProgressLayoutStyle);
    }

    public EasyProgressFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ProgressEmptyView progressEmptyView = new ProgressEmptyView(this, attrs, defStyle);
        setProgressHander(progressEmptyView);
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

    public View getEmptyView() {
        return emptyView;
    }

    public void showLoadingView(String message) {
        updata(false);
        mProgressHander.showLoadingView(message);
    }

    public void showEmptyView(String message) {
        updata(false);
        mProgressHander.showEmptyView(message);
    }

    public void showErrorView(String message) {
        updata(false);
        mProgressHander.showErrorView(message);
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

    void setProgressHander(IProgressHander progressHander) {
        mProgressHander = progressHander;
        setEmptyView(mProgressHander.getView());
    }

    private View emptyView;

    void updata(boolean hasData) {
        if (hasData && emptyView != null) {
            emptyView.setVisibility(View.GONE);
            EasyProgressFrameLayout.this.setVisibility(View.VISIBLE);
        } else {
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                EasyProgressFrameLayout.this.setVisibility(View.GONE);
            }
        }
    }


    void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
