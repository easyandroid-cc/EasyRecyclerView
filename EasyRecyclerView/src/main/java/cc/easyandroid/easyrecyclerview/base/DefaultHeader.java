package cc.easyandroid.easyrecyclerview.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cc.easyandroid.easyrecyclerview.R;
import cc.easyandroid.easyrecyclerview.abs.PullViewHandle;


/**
 * Created by Administrator on 2016/3/21.
 */
public class DefaultHeader extends BaseHeader {
    private Context context;
    private int rotationSrc;
    private int arrowSrc;

    private long freshTime;

    private final int ROTATE_ANIM_DURATION = 180;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;

    private TextView headerTitle;
    private TextView headerTime;
    private ImageView headerArrow;
    private ProgressBar headerProgressbar;

    public DefaultHeader(Context context) {
        this(context, R.drawable.progress_small, R.drawable.arrow);
    }

    public DefaultHeader(Context context, int rotationSrc, int arrowSrc) {
        this.context = context;
        this.rotationSrc = rotationSrc;
        this.arrowSrc = arrowSrc;

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(context).inflate(R.layout.default_header, null, true);
        headerTitle = (TextView) view.findViewById(R.id.default_header_title);
        headerTime = (TextView) view.findViewById(R.id.default_header_time);
        headerArrow = (ImageView) view.findViewById(R.id.default_header_arrow);
        headerProgressbar = (ProgressBar) view.findViewById(R.id.default_header_progressbar);
        headerArrow.setImageResource(arrowSrc);
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
        if (freshTime == 0) {
            freshTime = System.currentTimeMillis();
        } else {
            int m = (int) ((System.currentTimeMillis() - freshTime) / 1000 / 60);
            if (m >= 1 && m < 60) {
                headerTime.setText(m + "分钟前");
            } else if (m >= 60) {
                int h = m / 60;
                headerTime.setText(h + "小时前");
            } else if (m > 60 * 24) {
                int d = m / (60 * 24);
                headerTime.setText(d + "天前");
            } else if (m == 0) {
                headerTime.setText("刚刚");
            }
        }
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown, PullViewHandle pullViewHandle) {
        if (!pullViewHandle.isRefreshIng()) {
            if (!upORdown) {
                headerTitle.setText("松开刷新数据");
                headerArrow.setVisibility(View.VISIBLE);
                headerArrow.startAnimation(mRotateUpAnim);
            } else {
                headerTitle.setText("下拉刷新");
                headerArrow.setVisibility(View.VISIBLE);
                if(!pullViewHandle.isFirstMove()){//第一次移动时候监听不旋转
                    headerArrow.startAnimation(mRotateDownAnim);
                }
            }
        }
    }

    @Override
    public void onStartAnim() {
        freshTime = System.currentTimeMillis();
        headerTitle.setText("正在刷新");
        headerArrow.setVisibility(View.INVISIBLE);
        headerArrow.clearAnimation();
        headerProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishAnim() {
        headerTitle.setText("刷新成功");
        headerArrow.setVisibility(View.INVISIBLE);
        headerProgressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return 1500;
    }
}