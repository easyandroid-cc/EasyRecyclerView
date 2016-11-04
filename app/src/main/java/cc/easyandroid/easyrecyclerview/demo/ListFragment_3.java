package cc.easyandroid.easyrecyclerview.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.easyandroid.easyrecyclerview.core.progress.EasyProgressLinearLayout;
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener;

public class ListFragment_3 extends Fragment {

    EasyProgressLinearLayout easyProgressLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_3, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        easyProgressLayout = (EasyProgressLinearLayout) view.findViewById(R.id.easyProgressLayout);
        easyProgressLayout.showLoadingView();
        easyProgressLayout.setOnEasyProgressClickListener(new OnEasyProgressClickListener() {
            @Override
            public void onLoadingViewClick() {
                easyProgressLayout.showEmptyView();
            }

            @Override
            public void onEmptyViewClick() {
                easyProgressLayout.showErrorView();
            }

            @Override
            public void onErrorViewClick() {
                easyProgressLayout.showContentView();
            }
        });
    }


}
