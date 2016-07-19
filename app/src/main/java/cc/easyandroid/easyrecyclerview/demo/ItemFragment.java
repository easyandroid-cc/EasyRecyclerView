package cc.easyandroid.easyrecyclerview.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.easyandroid.easyrecyclerview.EasyRecyclerView;
import cc.easyandroid.easyrecyclerview.core.DefaultFooterHander;
import cc.easyandroid.easyrecyclerview.core.DefaultHeaderHander;
import cc.easyandroid.easyrecyclerview.demo.dummy.DummyContent;
import cc.easyandroid.easyrecyclerview.demo.dummy.DummyContent.DummyItem;
import cc.easyandroid.easyrecyclerview.listener.OnLoadMoreListener;
import cc.easyandroid.easyrecyclerview.listener.OnRefreshListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof EasyRecyclerView) {
            Context context = view.getContext();
            final EasyRecyclerView recyclerView = (EasyRecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setHeader(new DefaultHeaderHander(getContext()));
                recyclerView.setFooter(new DefaultFooterHander(getContext()));
                recyclerView.addItemDecoration(new RecycleViewDivider(view.getContext(), LinearLayoutManager.HORIZONTAL));
//                recyclerView.setdr
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            final MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    System.out.println("EasyRecyclerView 刷新开始");
                    recyclerView.finishLoadMore();

                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            adapter.addDatas(DummyContent.ITEMS);
                            recyclerView.finishRefresh();
                            System.out.println("EasyRecyclerView 刷结束");
                        }
                    }, 3000);
                }

            });
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.autoRefresh();
                }
            });
            recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(final EasyRecyclerView.FooterHander loadMoreView) {
                    System.out.println("EasyRecyclerView loadmore开始");
                    recyclerView.finishRefresh();
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addDatas(DummyContent.ITEMS);
                            loadMoreView.loadingCompleted();

                            System.out.println("EasyRecyclerView loadmore结束");
                        }
                    }, 3000);
                }
            });

        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
