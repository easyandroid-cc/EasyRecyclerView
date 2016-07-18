package cc.easyandroid.easyrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import cc.easyandroid.easyrecyclerview.abs.PullViewHandle;
import cc.easyandroid.easyrecyclerview.listener.OnLoadMoreListener;
import cc.easyandroid.easyrecyclerview.listener.OnLoadMoreScrollListener;
import cc.easyandroid.easyrecyclerview.listener.OnRefreshListener;
import cc.easyandroid.easyrecyclerview.listener.RefreshHeaderLayout;

/**
 * Created by aspsine on 16/3/3.
 */
public class EasyRecyclerView extends RecyclerView implements PullViewHandle {
    private static final String TAG = EasyRecyclerView.class.getSimpleName();

    private static final int STATUS_DEFAULT = 0;//默认

    private static final int STATUS_SWIPING_TO_REFRESH = 1;//下拉刷新

    private static final int STATUS_RELEASE_TO_REFRESH = 2;//松开加载

    private static final int STATUS_REFRESHING = 3;//正在刷新状态

    private static final int STATUS_COMPLETE = 4;//刷新或者加载完成的状态

    private int mStatus = STATUS_DEFAULT;//当前的状态

    private boolean mIsAutoRefreshing;

    private boolean mRefreshEnabled = false;

    private boolean mLoadMoreEnabled;

    private OnRefreshListener mOnRefreshListener;

    private OnLoadMoreListener mOnLoadMoreListener;

    private OnLoadMoreScrollListener mOnLoadMoreScrollListener;

    private RefreshHeaderLayout mRefreshHeaderContainer;

    private FrameLayout mLoadMoreFooterContainer;

    private View mRefreshHeaderView;

    private View mLoadMoreFooterView;

    private Scroller mScroller;

    private int mHeaderViewHeight; // header view's height

    private int mActivePointerId = -1;

    private int mLastTouchX = 0;

    private int mLastTouchY = 0;

    private boolean needResetAnim;//按下的时候关闭回弹

    private boolean refreshIng;

    private boolean firstMove;

    private DragHander mHeaderHander;

    private DragHander mFooterHander;


    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView, defStyle, 0);
        boolean refreshEnabled;
        boolean loadMoreEnabled;
        try {
            refreshEnabled = a.getBoolean(R.styleable.EasyRecyclerView_refreshEnabled, true);
            loadMoreEnabled = a.getBoolean(R.styleable.EasyRecyclerView_loadMoreEnabled, false);

        } finally {
            a.recycle();
        }
        mScroller = new Scroller(context, new DecelerateInterpolator());
        setRefreshEnabled(refreshEnabled);

        setLoadMoreEnabled(loadMoreEnabled);

        setStatus(STATUS_DEFAULT);//开始设置为默认
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                mLastTouchX = (int) (MotionEventCompat.getX(e, actionIndex) + 0.5f);
                mLastTouchY = (int) (MotionEventCompat.getY(e, actionIndex) + 0.5f);
            }
            break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                mActivePointerId = MotionEventCompat.getPointerId(e, actionIndex);
                mLastTouchX = (int) (MotionEventCompat.getX(e, actionIndex) + 0.5f);
                mLastTouchY = (int) (MotionEventCompat.getY(e, actionIndex) + 0.5f);
            }
            break;
            case MotionEventCompat.ACTION_POINTER_UP: {
                onPointerUp(e);
            }
            break;
        }

        return super.onInterceptTouchEvent(e);
    }

    /**
     * 处理多点中一个点up后的时间,详见父类
     *
     * @param e motionEvent e
     */
    private void onPointerUp(MotionEvent e) {
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        if (MotionEventCompat.getPointerId(e, actionIndex) == mActivePointerId) {
            // Pick a new pointer to pick up the slack.
            final int newIndex = actionIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(e, newIndex);
            mLastTouchX = getMotionEventX(e, newIndex);
            mLastTouchY = getMotionEventY(e, newIndex);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                firstMove = true;
                final int index = MotionEventCompat.getActionIndex(e);
                mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                mLastTouchX = getMotionEventX(e, index);
                mLastTouchY = getMotionEventY(e, index);
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                needResetAnim = false;      //按下的时候关闭回弹
                final int index = MotionEventCompat.findPointerIndex(e, mActivePointerId);
                if (index < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " + index + " not found. Did any MotionEvents get skipped?");
                    return false;
                }

                final int x = getMotionEventX(e, index);
                final int y = getMotionEventY(e, index);

                // final int dx = x - mLastTouchX; //not used
                final int dy = y - mLastTouchY;

                mLastTouchX = x;
                mLastTouchY = y;

                boolean triggerCondition = getFirstVisiblePosition() == 0 && isFingerDragging() && mRefreshEnabled;//第一个item显示
                if (triggerCondition) {//是否是在临界点，也就是可以下来的位置
                    return handlePull(e,dy);
                }
            }
            break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(e);
                mActivePointerId = MotionEventCompat.getPointerId(e, index);
                mLastTouchX = getMotionEventX(e, index);
                mLastTouchY = getMotionEventY(e, index);
            }
            break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onPointerUp(e);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                needResetAnim = true;      //松开的时候打开回弹
                if (getFirstVisiblePosition() == 0) {//抬起手指后复位
                    onFingerUpStartAnimating();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    private int getFirstVisiblePosition() {
        return ((RecyclerView.LayoutParams) getLayoutManager().getChildAt(0).getLayoutParams()).getViewLayoutPosition();
    }

    /**
     * 是否是在拖动
     *
     * @return 是否是在拖动
     */
    private boolean isFingerDragging() {
        return getScrollState() == SCROLL_STATE_DRAGGING;
    }


    private int getMotionEventX(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getX(e, pointerIndex) + 0.5f);
    }

    private int getMotionEventY(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getY(e, pointerIndex) + 0.5f);
    }

    /**
     * 处理下拉拖动
     * @param e  事件e
     * @param dy  拖动距离
     * @return  是否自己处理
     */
    private boolean handlePull(MotionEvent e,int dy){
        final int refreshHeaderContainerHeight = mRefreshHeaderContainer.getMeasuredHeight();
        final int refreshHeaderViewHeight = mHeaderViewHeight;
        mHeaderHander.onPreDrag(mRefreshHeaderView);
        if (dy > 0 && mStatus == STATUS_SWIPING_TO_REFRESH) {//下拉
            mHeaderHander.onDropAnim(mRefreshHeaderView, dy);
        } else if (dy < 0) {//上拉
            if ((mStatus == STATUS_SWIPING_TO_REFRESH || mStatus == STATUS_COMPLETE || mStatus == STATUS_REFRESHING) && refreshHeaderContainerHeight <= 0) {//这几种状态要跳出，让至空间自己移动，不然会卡住
                if (mStatus == STATUS_COMPLETE) {//完成上拉 再下来一个是下拉刷新  //完成后再往上拉要恢复到默认状态
                    setStatus(STATUS_DEFAULT);
                }
                return super.onTouchEvent(e);
            }
        }
        Log.e(TAG, "mStatus =" + mStatus);
        if (mStatus == STATUS_SWIPING_TO_REFRESH || mStatus == STATUS_RELEASE_TO_REFRESH || mStatus == STATUS_DEFAULT) {//完成后不能出现在这里，完成后上下拉动应该还是显示完成状态
            if (refreshHeaderContainerHeight >= refreshHeaderViewHeight) {//两种状态切换
                if (mStatus != STATUS_RELEASE_TO_REFRESH) {
                    setStatus(STATUS_RELEASE_TO_REFRESH);//释放刷新
                    mHeaderHander.onLimitDes(mRefreshHeaderView, false, this);//改变header 的文字箭头  释放刷新
                }
            } else {
                if (mStatus != STATUS_SWIPING_TO_REFRESH) {
                    mHeaderHander.onLimitDes(mRefreshHeaderView, true, this);//改变header 的文字箭头  下拉刷新
                    setStatus(STATUS_SWIPING_TO_REFRESH);//下拉刷新
                }
            }
        }

        fingerMove(dy);//TODO 移出来，让刷新的时候也可以拉动


        firstMove = false;
        super.onTouchEvent(e);
        return  true;
    }

    /**
     * 移动操作
     *
     * @param dy 移动的偏移量
     */
    private void fingerMove(int dy) {
        int ratioDy;
        if (dy > 0) {//减速
            ratioDy = (int) (((mHeaderHander.getDragMaxHeight(this) - mRefreshHeaderContainer.getHeight()) / (float) mHeaderHander.getDragMaxHeight(this)) * dy / 2);
        } else {//上滑速度1:1
            ratioDy = dy;
        }
        doMove(ratioDy);
    }

    /**
     * 移动操作
     *
     * @param dy 应要移动的y
     */
    private void doMove(int dy) {
        if (dy != 0) {
            int height = mRefreshHeaderContainer.getHeight() + dy;//改变header高度
            setRefreshHeaderContainerHeight(height);
        }
    }


    /**
     * 改变header的高度
     *
     * @param height 新的高度
     */
    private void setRefreshHeaderContainerHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        mRefreshHeaderContainer.getLayoutParams().height = height;
        mRefreshHeaderContainer.requestLayout();
        getLayoutManager().scrollToPosition(0);//让回滚的时候先让header缩回去
    }

    /**
     * 回到关闭状态
     */
    private void startScrollSwipingToRefreshStatusToDefaultStatus() {
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        setStatus(STATUS_DEFAULT);
        mScroller.startScroll(0, currentHeight, 0, -currentHeight, 120);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 释放进入到刷新状态                  复位到正在刷新 的位置
     */
    private void startScrollReleaseStatusToRefreshingStatus() {
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        mScroller.startScroll(0, currentHeight, 0, mHeaderViewHeight - currentHeight, 200);
        invalidate();
        setStatus(STATUS_REFRESHING);//设置当前状态是刷新状态
        refresh();
    }

    /**
     * 正在刷新时候下拉的会弹
     */
    private void startScrollRefreshingStatusToRefreshingStatus() {
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        mScroller.startScroll(0, currentHeight, 0, mHeaderViewHeight - currentHeight, 200);
    }


    //正在刷新时候就不再进行第二次回调
    synchronized void refresh() {
        if (!isRefreshIng()) {
            refreshIng = true;//标记正在刷新
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onRefresh();
            }
            mHeaderHander.onStartAnim();
        }
    }


    public void autoRefresh() {
        needResetAnim = true;
        startScrollReleaseStatusToRefreshingStatus();
    }

    /**
     * 重置控件位置，暴露给外部的方法，用于在刷新或者加载完成后调用
     */
    public void onFinishFreshAndLoad() {
        setStatus(STATUS_COMPLETE);//完成标识
        if (getFirstVisiblePosition() == 0 && needResetAnim) {
            startScrollSwipingToRefreshStatusToDefaultStatus();
        }
        refreshIng = false;
        if (mHeaderHander != null) mHeaderHander.onFinishAnim();
        if (mFooterHander != null) mFooterHander.onFinishAnim();

    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            setRefreshHeaderContainerHeight(mScroller.getCurrY());
        }
        super.computeScroll();
    }

    //抬起手指后复位
    private void onFingerUpStartAnimating() {

        if (mStatus == STATUS_RELEASE_TO_REFRESH) {
            startScrollReleaseStatusToRefreshingStatus();
        } else if (needResetAnim && (mStatus == STATUS_SWIPING_TO_REFRESH || mStatus == STATUS_COMPLETE || mStatus == STATUS_DEFAULT)) {
            startScrollSwipingToRefreshStatusToDefaultStatus();
        } else if (mStatus == STATUS_REFRESHING) {
            final int refreshHeaderContainerHeight = mRefreshHeaderContainer.getMeasuredHeight();
            if (refreshHeaderContainerHeight >= mHeaderViewHeight) {//滚动到刷新位置
                startScrollRefreshingStatusToRefreshingStatus();
            } else {//关闭
                startScrollSwipingToRefreshStatusToDefaultStatus();
            }
        }
    }

    public void setRefreshEnabled(boolean enabled) {
        this.mRefreshEnabled = enabled;
    }

    public void setLoadMoreEnabled(boolean enabled) {
        this.mLoadMoreEnabled = enabled;
        if (mLoadMoreEnabled) {
            if (mOnLoadMoreScrollListener == null) {
                mOnLoadMoreScrollListener = new OnLoadMoreScrollListener() {
                    @Override
                    public void onLoadMore(RecyclerView recyclerView) {

                        if (mOnLoadMoreListener != null && mStatus == STATUS_DEFAULT) {
                            mOnLoadMoreListener.onLoadMore(mLoadMoreFooterView);
                        }
                    }
                };
            } else {
                removeOnScrollListener(mOnLoadMoreScrollListener);
            }
            addOnScrollListener(mOnLoadMoreScrollListener);
        } else {
            if (mOnLoadMoreScrollListener != null) {
                removeOnScrollListener(mOnLoadMoreScrollListener);
            }
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }


    private void setRefreshHeaderView(View refreshHeaderView) {
        if (mRefreshHeaderView != null) {
            removeRefreshHeaderView();
        }
        if (mRefreshHeaderView != refreshHeaderView) {
            this.mRefreshHeaderView = refreshHeaderView;
            ensureRefreshHeaderContainer();
            mRefreshHeaderContainer.addView(refreshHeaderView);
        }
        // init header height
        if (mRefreshHeaderView == null) {
            throw new AssertionError();
        }
        mRefreshHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                int height = mHeaderHander.getDragSpringHeight(EasyRecyclerView.this);
                mHeaderViewHeight = height > 0 ? height : mRefreshHeaderView.getHeight();
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }


    public void setHeader(DragHander headerHander) {
        mHeaderHander = headerHander;
        setRefreshHeaderView(headerHander.getView());
    }

    public void setFooter(DragHander footerHander) {
        mFooterHander = footerHander;
        setLoadMoreFooterView(mHeaderHander.getView());
    }

    private void setLoadMoreFooterView(View loadMoreFooterView) {
        if (mLoadMoreFooterView != null) {
            removeLoadMoreFooterView();
        }
        if (mLoadMoreFooterView != loadMoreFooterView) {
            this.mLoadMoreFooterView = loadMoreFooterView;
            ensureLoadMoreFooterContainer();
            mLoadMoreFooterContainer.addView(loadMoreFooterView);
        }
    }

    public void setAdapter(Adapter adapter) {
        ensureRefreshHeaderContainer();
        ensureLoadMoreFooterContainer();

        if (adapter instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter baseRecyclerAdapter = (BaseRecyclerAdapter) adapter;
            baseRecyclerAdapter.addHeaderView(mRefreshHeaderContainer);
            TextView textView = new TextView(getContext());
            textView.setBackgroundColor(Color.RED);
            textView.setText("测试");
            textView.setHeight(100);
            textView.setGravity(Gravity.TOP);
            textView.setTextSize(10);
            baseRecyclerAdapter.addHeaderView(textView);
            baseRecyclerAdapter.addFooterView(mLoadMoreFooterContainer);
        }
        super.setAdapter(adapter);
    }

    private void ensureRefreshHeaderContainer() {
        if (mRefreshHeaderContainer == null) {
            mRefreshHeaderContainer = new RefreshHeaderLayout(getContext());
            mRefreshHeaderContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        }
    }

    private void ensureLoadMoreFooterContainer() {
        if (mLoadMoreFooterContainer == null) {
            mLoadMoreFooterContainer = new FrameLayout(getContext());
            mLoadMoreFooterContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void removeRefreshHeaderView() {
        if (mRefreshHeaderContainer != null) {
            mRefreshHeaderContainer.removeView(mRefreshHeaderView);
        }
    }

    private void removeLoadMoreFooterView() {
        if (mLoadMoreFooterContainer != null) {
            mLoadMoreFooterContainer.removeView(mLoadMoreFooterView);
        }
    }


    private void setStatus(int status) {
        this.mStatus = status;
    }

    @Override
    public boolean isLoadIng() {
        return false;
    }

    @Override
    public boolean isFirstMove() {
        return firstMove;
    }

    @Override
    public boolean isRefreshIng() {
        return refreshIng;
    }

    public interface DragHander {
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
        void onFinishAnim();
    }
}
