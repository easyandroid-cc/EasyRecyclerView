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
import android.support.v4.content.ContextCompat;
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
        footerTv = (TextView) view.findViewById(R.id.default_footer_title);
        footerBar = (ProgressBar) view.findViewById(R.id.default_footer_progressbar);
        footerBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        return view;
    }

    @Override
    public void showNormal() {
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
    public void showFail(Exception exception) {
        footerTv.setText(context.getResources().getString(R.string.easyrecyclerview_loadmorefail_click_to_loadmore));
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = false;
    }

    @Override
    public void fullLoadCompleted() {
        footerTv.setText(context.getResources().getString(R.string.easyrecyclerview_loadmore_fullcompleted));
        footerBar.setVisibility(View.GONE);
        loading = false;
        noMore = true;
    }

    public boolean onCanLoadMore() {
        return !loading && !noMore;
    }
}
