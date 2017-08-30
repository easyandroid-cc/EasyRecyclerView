package cc.easyandroid.easyrecyclerview.core;

import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

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

    public ProgressEmptyView(View view, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(view.getContext());

        TypedArray a = view.getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressEmptyView, defStyleAttr, 0);

        int loadingViewResId = a.getResourceId(R.styleable.ProgressEmptyView_easyLoadingView, R.layout.easyloadingview);// 正在加载的view
        int emptyViewResId = a.getResourceId(R.styleable.ProgressEmptyView_easyEmptyView, R.layout.easyemptyview);// 空数据的view
        int errorViewResId = a.getResourceId(R.styleable.ProgressEmptyView_easyErrorView, R.layout.easyerrorview);// 错误的view
        int easyEmptyContainerId = a.getResourceId(R.styleable.ProgressEmptyView_easyEmptyContainer, R.layout.easyemptycontainer);// 错误的view

        if (loadingViewResId > 0) {
            mLoadingView = mLayoutInflater.inflate(loadingViewResId, null);
        }

        if (emptyViewResId > 0) {
            mEmptyView = mLayoutInflater.inflate(emptyViewResId, null);
        }

        if (errorViewResId > 0) {
            mErrorView = mLayoutInflater.inflate(errorViewResId, null);
        }

        ViewGroup viewGroup = (ViewGroup) view.getParent();

        if (viewGroup != null) {
            mEmptyContainer = (ViewGroup) viewGroup.findViewById(easyEmptyContainerId);
        }
        if (mEmptyContainer == null && easyEmptyContainerId > 0) {
            mEmptyContainer = (ViewGroup) mLayoutInflater.inflate(easyEmptyContainerId, null);
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

    @Override
    public void showEmptyView(String message) {
        showView(State.EMPTY, message);
    }

    @Override
    public void showErrorView(String message) {
        showView(State.ERROR, message);
    }

    @Override

    public void showLoadingView(String message) {
        showView(State.LOADING, message);
    }

    public void showView(int state, String message) {

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
            if (!TextUtils.isEmpty(message)) {
                TextView loadingTextView = (TextView) mLoadingView.findViewById(R.id.progressMessageTextView);
                if (loadingTextView != null) {
                    loadingTextView.setText(message);
                }
            }

        }

        if (mEmptyView != null) {
            mEmptyView.setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(message)) {
                TextView emptyViewTextView = (TextView) mEmptyView.findViewById(R.id.progressMessageTextView);

                if (emptyViewTextView != null) {
                    emptyViewTextView.setText(message);
                }
            }

        }

        if (mErrorView != null) {
            mErrorView.setVisibility(showErrorView ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(message)) {
                TextView errorViewTextView = (TextView) mErrorView.findViewById(R.id.progressMessageTextView);
                if (errorViewTextView != null) {
                    errorViewTextView.setText(message);
                }
            }

        }
    }

    public void setOnEasyProgressClickListener(OnEasyProgressClickListener listener) {
        this.mOnEasyProgressClickListener = listener;
    }

}
