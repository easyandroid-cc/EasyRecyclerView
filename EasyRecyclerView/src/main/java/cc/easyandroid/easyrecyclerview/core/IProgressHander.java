package cc.easyandroid.easyrecyclerview.core;

import android.view.View;

import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

/**
 * Created by cgpllx on 2016/11/7.
 */
public interface IProgressHander {
    View getView();

    void showLoadingView(String message);

    void showEmptyView(String message);

    void showErrorView(String message);

    void setOnEasyProgressClickListener(OnEasyProgressClickListener listener);
}
