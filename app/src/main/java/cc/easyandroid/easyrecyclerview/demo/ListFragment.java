package cc.easyandroid.easyrecyclerview.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cc.easyandroid.easyrecyclerview.EasyRecycleViewDivider;
import cc.easyandroid.easyrecyclerview.EasyRecyclerAdapter;
import cc.easyandroid.easyrecyclerview.EasyRecyclerView;
import cc.easyandroid.easyrecyclerview.core.DefaultFooterHander;
import cc.easyandroid.easyrecyclerview.core.DefaultHeaderHander;
import cc.easyandroid.easyrecyclerview.demo.anim.AlphaInAnimation;
import cc.easyandroid.easyrecyclerview.demo.dummy.DummyContent;
import cc.easyandroid.easyrecyclerview.listener.OnLoadMoreListener;
import cc.easyandroid.easyrecyclerview.listener.OnRefreshListener;

public class ListFragment extends Fragment {
    Toast toast;
    MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new MyAdapter();
        adapter.setItemAnimation(new AlphaInAnimation());
        initView(view);
    }

    private void initView(View view) {
        EasyRecyclerView recyclerView = (EasyRecyclerView) view.findViewById(R.id.list);
        setupEasyRecyclerView(recyclerView);
        setupRefreshListener(recyclerView);
        setupLoadMoreListener(recyclerView);
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
                        adapter.addDatas(DummyContent.ITEMS);
                        recyclerView.finishLoadMore(EasyRecyclerView.FooterHander.LOADSTATUS_COMPLETED);
                    }
                }, 500);
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
                        adapter.setDatas(DummyContent.ITEMS);
                        recyclerView.finishRefresh(true);
                    }
                }, 1000);
            }

        });
    }

    private void setupEasyRecyclerView(EasyRecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));        // recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new EasyRecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL).setNotShowDividerCount(1, 1));//设置分割线
        recyclerView.setHeaderHander(new DefaultHeaderHander(getContext()));
        recyclerView.setFooterHander(new DefaultFooterHander(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setupOnItemClickListener(adapter);
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
}
