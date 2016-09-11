package cc.easyandroid.easyrecyclerview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import cc.easyandroid.easyrecyclerview.animation.BaseAnimation;
import cc.easyandroid.easyrecyclerview.animation.ViewHelper;

/**
 * Created by cgp
 */
public interface IEasyAdapter {


    void addFooterViewToLast(View lastFooterView);

    void addHeaderViewToFirst(View firstHeaderView);

    boolean isEmpty();

}
