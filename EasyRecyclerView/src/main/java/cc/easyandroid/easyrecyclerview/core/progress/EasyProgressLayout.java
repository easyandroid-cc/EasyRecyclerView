package cc.easyandroid.easyrecyclerview.core.progress;

import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

public interface EasyProgressLayout {

    void showLoadingView();

    void showEmptyView();

    void showErrorView();

    void showContentView();

    void setOnEasyProgressClickListener(OnEasyProgressClickListener listener);

}
