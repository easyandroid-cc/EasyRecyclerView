package cc.easyandroid.easyrecyclerview.core;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cc.easyandroid.easyrecyclerview.EasyRecyclerView;
import cc.easyandroid.easyrecyclerview.R;


public class DefaultFooterHander implements EasyRecyclerView.FooterHander {
    private Context context;

    private int rotationSrc;

    private TextView footerTv;

    private ProgressBar footerBar;

    private boolean loading = false;//加载中

    private boolean noMore = false;// 已经没有更多了

    public DefaultFooterHander(Context context) {
        this(context, R.drawable.progress_small);
    }

    public DefaultFooterHander(Context context, int rotationSrc) {
        this.context = context;
        this.rotationSrc = rotationSrc;
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(context).inflate(R.layout.default_footer, null, true);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
        footerTv = (TextView) view.findViewById(R.id.default_footer_title);
        footerTv.setText(context.getResources().getString(R.string.easyrecyclerview_click_to_loadmore));
        footerBar = (ProgressBar) view.findViewById(R.id.default_footer_progressbar);
        footerBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        return view;
    }

    @Override
    public void showLoadCompleted() {
        footerTv.setText(context.getResources().getString(R.string.easyrecyclerview_click_to_loadmore));
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = false;
    }

    @Override
    public void showLoading() {
        footerTv.setText(context.getResources().getString(R.string.easyrecyclerview_loadmoreing));
        footerBar.setVisibility(View.VISIBLE);
        loading = true;
        noMore = false;
    }

    @Override
    public void showLoadFail() {
        footerTv.setText(context.getResources().getString(R.string.easyrecyclerview_loadmorefail_click_to_loadmore));
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = false;
    }

    @Override
    public void showLoadFullCompleted() {
        footerTv.setText(context.getResources().getString(R.string.easyrecyclerview_loadmore_fullcompleted));
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = true;
    }

    public boolean onCanLoadMore() {
        return !loading && !noMore;
    }
}
