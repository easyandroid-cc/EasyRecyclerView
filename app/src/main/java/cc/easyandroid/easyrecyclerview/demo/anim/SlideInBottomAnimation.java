package cc.easyandroid.easyrecyclerview.demo.anim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import cc.easyandroid.easyrecyclerview.animation.BaseAnimation;


public class SlideInBottomAnimation implements BaseAnimation {


    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
        };
    }
}
