package cc.easyandroid.easyrecyclerview.core;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cc.easyandroid.easyrecyclerview.EasyRecyclerView;
import cc.easyandroid.easyrecyclerview.R;


public class DefaultHeaderHander implements EasyRecyclerView.HeaderHander {
    private Context context;
    private int rotationSrc;
    private int arrowSrc;
    private final static String KEY_SHAREDPREFERENCES = "last_refresh_time";
    private String mLastUpdateTimeKey;

    private final int ROTATE_ANIM_DURATION = 180;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;

    private View headertimebox;
    private TextView headerTitle;
    private TextView headerTime;
    private ImageView headerArrow;
    private ProgressBar headerProgressbar;

    public  DefaultHeaderHander(Context context) {
        this(context, R.drawable.progress_small, R.drawable.arrow);
    }

    public DefaultHeaderHander(Context context, int rotationSrc, int arrowSrc) {
        this.context = context;
        this.rotationSrc = rotationSrc;
        this.arrowSrc = arrowSrc;

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(context).inflate(R.layout.default_header, null, true);
        headertimebox = view.findViewById(R.id.default_header_timebox);
        headerTitle = (TextView) view.findViewById(R.id.default_header_title);
        headerTime = (TextView) view.findViewById(R.id.default_header_time);
        headerArrow = (ImageView) view.findViewById(R.id.default_header_arrow);
        headerProgressbar = (ProgressBar) view.findViewById(R.id.default_header_progressbar);
        headerProgressbar.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        headerArrow.setImageResource(arrowSrc);
        return view;
    }


    @Override
    public void onPreDrag(View rootView) {

        if (mLastUpdateTime == 0) {
            mLastUpdateTime = getFreshTime(rootView.getContext());
        }
        if (mLastUpdateTime == 0) {
            headertimebox.setVisibility(View.GONE);
        } else {
            int m = (int) ((System.currentTimeMillis() - mLastUpdateTime) / 1000 / 60);
            if (m >= 1 && m < 60) {
                headerTime.setText(m + context.getResources().getString(R.string.easyrecyclerview_minutes_ago));
            } else if (m >= 60) {
                int h = m / 60;
                headerTime.setText(h + context.getResources().getString(R.string.easyrecyclerview_hours_ago));
            } else if (m > 60 * 24) {
                int d = m / (60 * 24);
                headerTime.setText(d + context.getResources().getString(R.string.easyrecyclerview_day_ago));
            } else if (m == 0) {
                headerTime.setText(context.getResources().getString(R.string.easyrecyclerview_just));
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
                headerTitle.setText(context.getResources().getString(R.string.easyrecyclerview_release_to_refresh));
                headerArrow.setVisibility(View.VISIBLE);
                headerArrow.startAnimation(mRotateUpAnim);
            } else {
                headerTitle.setText(context.getResources().getString(R.string.easyrecyclerview_pull_down_to_refresh));
                headerArrow.setVisibility(View.VISIBLE);
                if (!pullViewHandle.isFirstMove()) {//第一次移动时候监听不旋转
                    headerArrow.startAnimation(mRotateDownAnim);
                }
            }
        }
    }

    long mLastUpdateTime = 0l;

    public void setLastUpdateTimeKey(String mLastUpdateTimeKey) {
        this.mLastUpdateTimeKey = mLastUpdateTimeKey;
    }

    void saveFreshTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREFERENCES, 0);
        if (!TextUtils.isEmpty(mLastUpdateTimeKey)) {
            mLastUpdateTime = System.currentTimeMillis();
            sharedPreferences.edit().putLong(mLastUpdateTimeKey, mLastUpdateTime).commit();
        }
    }

    long getFreshTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREFERENCES, 0);
        if (!TextUtils.isEmpty(mLastUpdateTimeKey)) {
            return sharedPreferences.getLong(mLastUpdateTimeKey, 0);
        }
        return 0;
    }

    @Override
    public void onStartAnim() {
        headerTitle.setText(context.getResources().getString(R.string.easyrecyclerview_refreshing));
        headerArrow.setVisibility(View.INVISIBLE);
        headerArrow.clearAnimation();
        headerProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishAnim(boolean refreshSuccess) {
        if (refreshSuccess) {
            headerTitle.setText(context.getResources().getString(R.string.easyrecyclerview_refresh_success));
        } else {
            headerTitle.setText(context.getResources().getString(R.string.easyrecyclerview_refresh_fail));
        }
        headerArrow.setVisibility(View.INVISIBLE);
        headerProgressbar.setVisibility(View.INVISIBLE);
        saveFreshTime(headerTitle.getContext());
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return 1500;
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        return 0;
    }
}