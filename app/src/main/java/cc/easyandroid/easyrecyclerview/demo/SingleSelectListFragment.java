package cc.easyandroid.easyrecyclerview.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;
import cc.easyandroid.easyrecyclerview.EasyRecycleViewDivider;
import cc.easyandroid.easyrecyclerview.EasyRecyclerAdapter;
import cc.easyandroid.easyrecyclerview.EasyRecyclerView;
import cc.easyandroid.easyrecyclerview.core.DefaultFooterHander;
import cc.easyandroid.easyrecyclerview.core.DefaultHeaderHander;
import cc.easyandroid.easyrecyclerview.demo.anim.AlphaInAnimation;
import cc.easyandroid.easyrecyclerview.demo.dummy.DummyContent;
import cc.easyandroid.easyrecyclerview.demo.text.MyHolder;
import cc.easyandroid.easyrecyclerview.demo.text.MyHolder_sticky;
import cc.easyandroid.easyrecyclerview.items.IFlexible;
import cc.easyandroid.easyrecyclerview.listener.OnLoadMoreListener;
import cc.easyandroid.easyrecyclerview.listener.OnRefreshListener;

public class SingleSelectListFragment extends Fragment implements EasyFlexibleAdapter.OnItemClickListener {
    Toast toast;
    EasyFlexibleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new EasyFlexibleAdapter(this);
        adapter.setMode(EasyFlexibleAdapter.MODE_SINGLE);
        adapter.setItemAnimation(new AlphaInAnimation());
        initView(view);
    }

    private void initView(View view) {
        final EasyRecyclerView recyclerView = (EasyRecyclerView) view.findViewById(R.id.list);
        setupEasyRecyclerView(recyclerView);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                        adapter.setDatas(DummyContent.ITEMS);
                List<IFlexible> items = new ArrayList<IFlexible>();
                items.add(new MyHolder_sticky(22));
                for (int i = 0; i < 30; i++) {
                    items.add(new MyHolder(i + 200));
                }
//                        adapter.addHeaderItem(new MyHolder_sticky(22));
                items.add(new MyHolder_sticky(22));
                recyclerView.finishRefresh(true);
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }
        }, 1000);
    }


    private void setupEasyRecyclerView(EasyRecyclerView recyclerView) {
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//          recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setRestItemCountToLoadMore(100);

        recyclerView.addItemDecoration(new EasyRecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL).setNotShowDividerCount(1, 1));//设置分割线
        recyclerView.setHeaderHander(new DefaultHeaderHander(getContext()));
        recyclerView.setFooterHander(new DefaultFooterHander(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
//        recyclerView.getRecycledViewPool().setMaxRecycledViews();
    }

    private void setupOnItemClickListener(MyAdapter adapter) {
        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        adapter.setOnItemClickListener(new EasyRecyclerAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(EasyRecyclerAdapter<String> adapter, View view, int position) {
                if (toast != null) {
                    toast.setText(position + "");
                } else {
                    toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
//        adapter.setOnItemClickListener(new EasyRecyclerAdapter.OnItemClickListener<String>() {
//            @Override
//            public void onItemClick(View view, int position) {
//                if (toast != null) {
//                    toast.setText(position + "");
//                } else {
//                    toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
//                }
//                toast.show();
//            }
//        });
    }

    @Override
    public boolean onItemClick(View view, int position) {
        return false;
    }
}