package cc.easyandroid.easyrecyclerview.items;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;

/**
 * 一个空接口，用来区分是header
 */
public class RefreshOrLoadmoreFlexible extends AbstractEasyFlexibleItem {
    private View mRefreshOrLoadmore;

    public RefreshOrLoadmoreFlexible(View refreshOrLoadmore) {
        this.mRefreshOrLoadmore = refreshOrLoadmore;
    }

    @Override
    public int getLayoutRes() {
        return hashCode();
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new RecyclerView.ViewHolder(mRefreshOrLoadmore) {
        };
    }

    @Override
    public void bindViewHolder(EasyFlexibleAdapter adapter, RecyclerView.ViewHolder holder, int position, List payloads) {
        //not use
    }
}