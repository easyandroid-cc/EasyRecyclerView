package cc.easyandroid.easyrecyclerview.core;

import android.view.View;

import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

/**
 * Created by cgpllx on 2016/11/7.
 */
public interface ProgressHander {
    View getView();

    void showLoadingView();

    void showEmptyView();

    void showErrorView();

    void setOnEasyProgressClickListener(OnEasyProgressClickListener listener);
}
