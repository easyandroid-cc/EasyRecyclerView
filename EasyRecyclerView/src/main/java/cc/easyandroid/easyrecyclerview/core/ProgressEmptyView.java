package cc.easyandroid.easyrecyclerview.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import cc.easyandroid.easyrecyclerview.R;
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

/**
 */
public class ProgressEmptyView implements IProgressHander {

    private View mLoadingView;

    private View mEmptyView;

    private View mErrorView;

    private ViewGroup mEmptyContainer;

    private OnEasyProgressClickListener mOnEasyProgressClickListener;

    public ProgressEmptyView(Context context) {
        this(context, null);
    }

    public ProgressEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ProgressEmptyViewStyle);
    }

    public ProgressEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressEmptyView, defStyleAttr, 0);

        int loadingViewResId = a.getResourceId(R.styleable.ProgressEmptyView_easyLoadingView, R.layout.easyloadingview);// 正在加载的view
        int emptyViewResId = a.getResourceId(R.styleable.ProgressEmptyView_easyEmptyView, R.layout.easyemptyview);// 空数据的view
        int errorViewResId = a.getResourceId(R.styleable.ProgressEmptyView_easyErrorView, R.layout.easyerrorview);// 错误的view
        int easyEmptyContainerId = a.getResourceId(R.styleable.ProgressEmptyView_easyErrorView, R.layout.easyemptycontainer);// 错误的view

        if (loadingViewResId > 0) {
            mLoadingView = mLayoutInflater.inflate(loadingViewResId, null);
        }

        if (emptyViewResId > 0) {
            mEmptyView = mLayoutInflater.inflate(emptyViewResId, null);
        }

        if (errorViewResId > 0) {
            mErrorView = mLayoutInflater.inflate(errorViewResId, null);
        }

        if (easyEmptyContainerId > 0) {
            mEmptyContainer = (ViewGroup) mLayoutInflater.inflate(easyEmptyContainerId, null);
        } else {
            mEmptyContainer = new FrameLayout(context, attrs, defStyleAttr);
        }
        mEmptyContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        a.recycle();

        initProgress();
    }


    private void initProgress() {

        setupEmtpyView();

        setupErrorView();

        setupLoadingView();

    }

    private void setupLoadingView() {
        if (mLoadingView != null) {
            mLoadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mEmptyContainer.addView(mLoadingView);
            View view = mLoadingView.findViewById(R.id.progressCanClickView);
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnEasyProgressClickListener != null) {
                            mOnEasyProgressClickListener.onLoadingViewClick();
                        }
                    }
                });
            } else {
                mLoadingView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnEasyProgressClickListener != null) {
                            mOnEasyProgressClickListener.onLoadingViewClick();
                        }
                    }
                });
            }
            mLoadingView.setVisibility(View.VISIBLE);
        }
    }

    private void setupErrorView() {
        if (mErrorView != null) {
            mEmptyContainer.addView(mErrorView);
            View view = mErrorView.findViewById(R.id.progressCanClickView);
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnEasyProgressClickListener != null) {
                            mOnEasyProgressClickListener.onErrorViewClick();
                        }
                    }
                });
            } else {
                mErrorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnEasyProgressClickListener != null) {
                            mOnEasyProgressClickListener.onErrorViewClick();
                        }
                    }
                });
            }
            mErrorView.setVisibility(View.GONE);
        }
    }

    private void setupEmtpyView() {
        if (mEmptyView != null) {
            mEmptyContainer.addView(mEmptyView);
            View view = mEmptyView.findViewById(R.id.progressCanClickView);
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnEasyProgressClickListener != null) {
                            mOnEasyProgressClickListener.onEmptyViewClick();
                        }
                    }
                });
            } else {
                mEmptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnEasyProgressClickListener != null) {
                            mOnEasyProgressClickListener.onEmptyViewClick();
                        }
                    }
                });
            }
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public interface State {
        int LOADING = 0, EMPTY = 1, ERROR = 2;
    }

    @Override
    public View getView() {
        return mEmptyContainer;
    }

    public void showLoadingView() {
        showView(State.LOADING);
    }

    public void showEmptyView() {
        showView(State.EMPTY);
    }

    public void showErrorView() {
        showView(State.ERROR);
    }

    public void showView(int state) {

        boolean showLoadingView = false;
        boolean showEmptyView = false;
        boolean showErrorView = false;

        switch (state) {
            case State.LOADING:
                showLoadingView = true;
                break;
            case State.EMPTY:
                showEmptyView = true;
                break;
            case State.ERROR:
                showErrorView = true;
                break;
        }

        if (mLoadingView != null) {
            mLoadingView.setVisibility(showLoadingView ? View.VISIBLE : View.GONE);
        }

        if (mEmptyView != null) {
            mEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
        }

        if (mErrorView != null) {
            mErrorView.setVisibility(showErrorView ? View.VISIBLE : View.GONE);
        }
    }

    public void setOnEasyProgressClickListener(OnEasyProgressClickListener listener) {
        this.mOnEasyProgressClickListener = listener;
    }

}
