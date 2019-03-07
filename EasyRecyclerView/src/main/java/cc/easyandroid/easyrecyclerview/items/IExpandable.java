package cc.easyandroid.easyrecyclerview.items;

import java.util.List;

import cc.easyandroid.easyrecyclerview.holders.ExpandableViewHolder;

public interface IExpandable<VH extends ExpandableViewHolder, S extends IFlexible>
        extends IFlexible<VH> {


    boolean isExpanded();

    void setExpanded(boolean expanded);


    /**
     * s 不能喝其他组里面的s equals 要 ==false
     * @return
     */
    List<S> getSubItems();

}