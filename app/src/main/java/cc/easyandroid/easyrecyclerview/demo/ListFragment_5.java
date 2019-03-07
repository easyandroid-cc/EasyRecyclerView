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
import cc.easyandroid.easyrecyclerview.demo.text.MyHolder;
import cc.easyandroid.easyrecyclerview.demo.text.MyHolder_Expand;
import cc.easyandroid.easyrecyclerview.demo.text.MyHolder_sticky;
import cc.easyandroid.easyrecyclerview.items.IFlexible;
import cc.easyandroid.easyrecyclerview.listener.OnLoadMoreListener;
import cc.easyandroid.easyrecyclerview.listener.OnRefreshListener;

public class ListFragment_5 extends Fragment {
    Toast toast;
    EasyFlexibleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_2, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new EasyFlexibleAdapter();
        adapter.setMode(EasyFlexibleAdapter.MODE_MULTI);
//        adapter.set

//        adapter.initializeListeners(new EasyFlexibleAdapter.OnItemClickListener() {
//            @Override
//            public boolean onItemClick(int position) {
//                if (toast != null) {
//                    toast.setText(position + "");
//                } else {
//                    toast = Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT);
//                }
//                toast.show();
//
//                return false;
//            }
//        });

        adapter.setItemAnimation(new AlphaInAnimation());
        initView(view);
        adapter.setStickyHeaders(true);
    }

    private void initView(View view) {
        EasyRecyclerView recyclerView = (EasyRecyclerView) view.findViewById(R.id.list);
//        recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.fragment_item, 0);
        setupEasyRecyclerView(recyclerView);
        setupRefreshListener(recyclerView);
//        setupLoadMoreListener(recyclerView);
        recyclerView.autoRefresh();

    }

    private void setupLoadMoreListener(final EasyRecyclerView recyclerView) {
        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final EasyRecyclerView.FooterHander loadMoreView) {
                recyclerView.finishRefresh(true);
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<MyHolder> items = new ArrayList<MyHolder>();
                        for (int i = 0; i < 100; i++) {
                            items.add(new MyHolder(i + 200));
                        }
                        adapter.addItems(items);
                        recyclerView.finishLoadMore(EasyRecyclerView.FooterHander.LOADSTATUS_COMPLETED);
                    }
                }, 1000);
            }
        });
    }

    /**
     * 设置刷新监听
     *
     * @param recyclerView
     */
    private void setupRefreshListener(final EasyRecyclerView recyclerView) {
        recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.finishLoadMore(EasyRecyclerView.FooterHander.LOADSTATUS_COMPLETED);
                recyclerView.showLoadingView();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        adapter.setDatas(DummyContent.ITEMS);
                        List<IFlexible> items = new ArrayList<IFlexible>();
//                        items.add(new MyHolder_Expand());
                        items.add(new MyHolder_sticky(22));

                        for (int i = 0; i < 2; i++) {
                            items.add(new MyHolder(i + 200));
                        }
                        items.add(new MyHolder_Expand());
                        items.add(new MyHolder_Expand());
                        items.add(new MyHolder_Expand());
//                        adapter.addHeaderItem(new MyHolder_sticky(22));
                        items.add(new MyHolder_sticky(22));
                        recyclerView.finishRefresh(true);
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                    }
                }, 1000);
            }

        });
    }

    private void setupEasyRecyclerView(EasyRecyclerView recyclerView) {
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//          recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new EasyRecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL).setNotShowDividerCount(1, 1));//设置分割线
        recyclerView.setHeaderHander(new DefaultHeaderHander(getContext()));
        recyclerView.setFooterHander(new DefaultFooterHander(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        setupOnItemClickListener(adapter);
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
                    toast = Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }
}
