/*
Copyright 2015 Chanven

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cc.easyandroid.easyrecyclerview.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cc.easyandroid.easyrecyclerview.EasyRecyclerView;
import cc.easyandroid.easyrecyclerview.R;


/**
 * default load more view
 */
public class DefaultFooterHander implements EasyRecyclerView.FooterHander {
    Context context;

    public DefaultFooterHander(Context context) {
        this.context = context;
    }

    TextView footerTv;
    ProgressBar footerBar;


    @Override
    public View getView() {
//        View view = LayoutInflater.from(context).inflate(R.layout.default_footer, null, true);
//        footerTv = (TextView) view.findViewById(R.id.default_footer_title);
//        footerBar = (ProgressBar) view.findViewById(R.id.default_footer_progressbar);
        View view = LayoutInflater.from(context).inflate(R.layout.default_footer, null, true);
        footerTv = (TextView) view.findViewById(R.id.default_footer_title);
        footerBar = (ProgressBar) view.findViewById(R.id.default_footer_progressbar);
//        footerProgressbar.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        return view;
    }

    private boolean loading = false;//加载中
    private boolean noMore = false;// 已经没有更多了

    @Override
    public void showNormal() {
        footerTv.setText("点击加载更多");
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = false;
    }

    @Override
    public void showLoading() {
        footerTv.setText("正在加载中...");
        footerBar.setVisibility(View.VISIBLE);
        loading = true;
        noMore = false;
    }

    @Override
    public void showFail(Exception exception) {
        footerTv.setText("加载失败，点击重新");
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = false;
    }

    @Override
    public void loadingCompleted() {
        footerTv.setText("已经加载完毕");
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = true;
    }

    public boolean onCanLoadMore() {
        return !loading && !noMore;
    }
}
