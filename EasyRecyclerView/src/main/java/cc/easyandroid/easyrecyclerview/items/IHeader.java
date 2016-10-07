package cc.easyandroid.easyrecyclerview.items;

import cc.easyandroid.easyrecyclerview.holders.FlexibleViewHolder;

/**
 * 一个空接口，用来区分是header
 */
public interface IHeader<VH extends FlexibleViewHolder> extends IFlexible<VH> {
    boolean isSticky();
}