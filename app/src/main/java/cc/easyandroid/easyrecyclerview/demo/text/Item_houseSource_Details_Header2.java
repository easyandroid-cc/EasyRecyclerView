package cc.easyandroid.easyrecyclerview.demo.text;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;
import cc.easyandroid.easyrecyclerview.EasyRecyclerView;
import cc.easyandroid.easyrecyclerview.demo.R;
import cc.easyandroid.easyrecyclerview.holders.FlexibleViewHolder;
import cc.easyandroid.easyrecyclerview.items.IFlexible;
import cc.easyandroid.easyrecyclerview.items.IHeaderSpanFill;

public class Item_houseSource_Details_Header2 implements IFlexible<Item_houseSource_Details_Header2.ViewHolder>  {

    public Item_houseSource_Details_Header2() {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean b) {

    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(boolean hidden) {

    }

    public int getSpanSize(int spanCount, int position) {
        return spanCount;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void setSelectable(boolean b) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_house_source_details_header2;
    }

    @Override
    public ViewHolder createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(EasyFlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.setData();
    }

    public class ViewHolder extends FlexibleViewHolder {
        EasyRecyclerView headerView;
        EasyFlexibleAdapter easyFlexibleAdapter = new EasyFlexibleAdapter();

        public ViewHolder(final View view, EasyFlexibleAdapter adapter) {
            super(view, adapter);
            headerView = view.findViewById(R.id.headerView);
            headerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            headerView.setAdapter(easyFlexibleAdapter);
            headerView.showEmptyView();
//            easyFlexibleAdapter.addHeaderItem(new Item_HouseSource_Details_UploadImage_AddItem());

        }

        public void setData( ) {
            ArrayList<IFlexible> indexAdInfos = new ArrayList<>();
            easyFlexibleAdapter.setItemsAndNotifyChanged(indexAdInfos);

        }
    }

}
