package cc.easyandroid.easyrecyclerview.core;

import android.view.View;

/**
 * Created by cgpllx on 2016/7/7.
 */
public interface PullViewHandle {
    boolean isLoadIng();

    boolean isRefreshIng();

    boolean isFirstMove();

    interface HeaderHander {
        View getView();

        int getDragMaxHeight(View rootView);

        int getDragSpringHeight(View rootView);

        void onPreDrag(View rootView);//初始化时间操作

        /**
         * 手指拖动控件过程中的回调，用户可以根据拖动的距离添加拖动过程动画
         *
         * @param dy 拖动距离，下拉为+，上拉为-
         */
        void onDropAnim(View rootView, int dy);

        /**
         * 手指拖动控件过程中每次抵达临界点时的回调，用户可以根据手指方向设置临界动画
         *
         * @param upORdown 是上拉还是下拉
         */
        void onLimitDes(View rootView, boolean upORdown, PullViewHandle pullViewHandle);

        /**
         * 拉动超过临界点后松开时回调
         */
        void onStartAnim();

        /**
         * 头(尾)已经全部弹回时回调
         */
        void onFinishAnim(boolean refreshSuccess);
    }
}