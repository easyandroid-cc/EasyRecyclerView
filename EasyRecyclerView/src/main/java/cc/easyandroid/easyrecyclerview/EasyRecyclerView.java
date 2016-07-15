package cc.easyandroid.easyrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

    private static final boolean DEBUG = false;

    private int mStatus;

    private boolean mIsAutoRefreshing;

    private boolean mRefreshEnabled = true;

    private boolean mLoadMoreEnabled;

    private int mRefreshFinalMoveOffset;

    private OnRefreshListener mOnRefreshListener;

    private OnLoadMoreListener mOnLoadMoreListener;

    private OnLoadMoreScrollListener mOnLoadMoreScrollListener;

    private RefreshHeaderLayout mRefreshHeaderContainer;

    private FrameLayout mLoadMoreFooterContainer;


    private View mRefreshHeaderView;

    private View mLoadMoreFooterView;

    Scroller mScroller;
    private int mHeaderViewHeight; // header view's height

    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView, defStyle, 0);
        @LayoutRes int refreshHeaderLayoutRes = -1;
        @LayoutRes int loadMoreFooterLayoutRes = -1;
        int refreshFinalMoveOffset = -1;
        boolean refreshEnabled;
        boolean loadMoreEnabled;
        try {
            refreshEnabled = a.getBoolean(R.styleable.EasyRecyclerView_refreshEnabled, true);
            loadMoreEnabled = a.getBoolean(R.styleable.EasyRecyclerView_loadMoreEnabled, false);
            refreshHeaderLayoutRes = a.getResourceId(R.styleable.EasyRecyclerView_refreshHeaderLayout, -1);
            loadMoreFooterLayoutRes = a.getResourceId(R.styleable.EasyRecyclerView_loadMoreFooterLayout, -1);
            refreshFinalMoveOffset = a.getDimensionPixelOffset(R.styleable.EasyRecyclerView_refreshFinalMoveOffset, -1);

        } finally {
            a.recycle();
        }
        mScroller = new Scroller(context, new DecelerateInterpolator());
        setRefreshEnabled(refreshEnabled);

        setLoadMoreEnabled(loadMoreEnabled);

        if (refreshHeaderLayoutRes != -1) {
            setRefreshHeaderView(refreshHeaderLayoutRes);
        }
        if (loadMoreFooterLayoutRes != -1) {
            setLoadMoreFooterView(loadMoreFooterLayoutRes);
        }
        if (refreshFinalMoveOffset != -1) {
            setRefreshFinalMoveOffset(refreshFinalMoveOffset);
        }
        setStatus(STATUS_DEFAULT);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mRefreshHeaderView != null) {
            if (mRefreshHeaderView.getMeasuredHeight() > mRefreshFinalMoveOffset) {
                mRefreshFinalMoveOffset = 0;
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

    public void setRefreshing(boolean refreshing) {
        if (mStatus == STATUS_DEFAULT && refreshing) {
            System.out.println("IRecyclerView 1111111");
            this.mIsAutoRefreshing = true;
            setStatus(STATUS_SWIPING_TO_REFRESH);
            startScrollDefaultStatusToRefreshingStatus();
        } else if (mStatus == STATUS_REFRESHING && !refreshing) {
            System.out.println("IRecyclerView 222222222");
            this.mIsAutoRefreshing = false;
            startScrollRefreshingStatusToDefaultStatus();
        } else {
            System.out.println("IRecyclerView 3333333333");
            this.mIsAutoRefreshing = false;
            Log.e(TAG, "isRefresh = " + refreshing + " current status = " + mStatus);
        }
    }

    public void setRefreshFinalMoveOffset(int refreshFinalMoveOffset) {
        this.mRefreshFinalMoveOffset = refreshFinalMoveOffset;
    }

    public void setRefreshHeaderView(View refreshHeaderView) {
//        if (!isRefreshTrigger(refreshHeaderView)) {
//            throw new ClassCastException("Refresh header view must be an implement of RefreshTrigger");
//        }

        if (mRefreshHeaderView != null) {
            removeRefreshHeaderView();
        }
        if (mRefreshHeaderView != refreshHeaderView) {
            this.mRefreshHeaderView = refreshHeaderView;
            ensureRefreshHeaderContainer();
            mRefreshHeaderContainer.addView(refreshHeaderView);
        }
        // init header height
        mRefreshHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight = mRefreshHeaderView.getHeight();
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    public void setRefreshHeaderView(@LayoutRes int refreshHeaderLayoutRes) {
        ensureRefreshHeaderContainer();
        final View refreshHeader = LayoutInflater.from(getContext()).inflate(refreshHeaderLayoutRes, mRefreshHeaderContainer, false);
        if (refreshHeader != null) {
            setRefreshHeaderView(refreshHeader);
        }
    }

    DragHander mHeaderHander;

    public void setHeader(DragHander headerHander) {
        mHeaderHander = headerHander;
        setRefreshHeaderView(headerHander.getView());
    }

    public void setLoadMoreFooterView(View loadMoreFooterView) {
        if (mLoadMoreFooterView != null) {
            removeLoadMoreFooterView();
        }
        if (mLoadMoreFooterView != loadMoreFooterView) {
            this.mLoadMoreFooterView = loadMoreFooterView;
            ensureLoadMoreFooterContainer();
            mLoadMoreFooterContainer.addView(loadMoreFooterView);
        }
    }

    public void setLoadMoreFooterView(@LayoutRes int loadMoreFooterLayoutRes) {
        ensureLoadMoreFooterContainer();
        final View loadMoreFooter = LayoutInflater.from(getContext()).inflate(loadMoreFooterLayoutRes, mLoadMoreFooterContainer, false);
        if (loadMoreFooter != null) {
            setLoadMoreFooterView(loadMoreFooter);
        }
    }

    public View getRefreshHeaderView() {
        return mRefreshHeaderView;
    }

    public View getLoadMoreFooterView() {
        return mLoadMoreFooterView;
    }

    public void setAdapter(Adapter adapter) {
        ensureRefreshHeaderContainer();
        ensureLoadMoreFooterContainer();
        System.out.println("BaseRecyclerAdapter=" + adapter);
        System.out.println("BaseRecyclerAdapter ddd =" + (adapter instanceof BaseRecyclerAdapter));
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


//    private boolean isRefreshTrigger(View refreshHeaderView) {
//        return refreshHeaderView instanceof RefreshTrigger;
//    }

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

    private int mActivePointerId = -1;
    private int mLastTouchX = 0;
    private int mLastTouchY = 0;

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

    boolean needResetAnim;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
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

                final int dx = x - mLastTouchX;
                final int dy = y - mLastTouchY;

                mLastTouchX = x;
                mLastTouchY = y;
//                System.out.println("EasyRecyclerView isEnabled()=" + isEnabled());
//                System.out.println("EasyRecyclerView mRefreshEnabled =" + mRefreshEnabled);
//                System.out.println("EasyRecyclerView mRefreshHeaderView=" + mRefreshHeaderView);
//                System.out.println("EasyRecyclerView isFingerDragging()=" + isFingerDragging());
//                System.out.println("EasyRecyclerView canTriggerRefresh()=" + canTriggerRefresh());
                boolean triggerCondition = isEnabled() && mRefreshEnabled && mRefreshHeaderView != null && isFingerDragging() && canTriggerRefresh();
                if (DEBUG) {
                    Log.i(TAG, "triggerCondition = " + triggerCondition + "; mStatus = " + mStatus + "; dy = " + dy);
                }
                triggerCondition = getFirstVisiblePosition() == 0;
                if (triggerCondition) {//是否是在临界点，也就是可以下来的位置

                    final int refreshHeaderContainerHeight = mRefreshHeaderContainer.getMeasuredHeight();
                    final int refreshHeaderViewHeight = mHeaderViewHeight;

                    if (dy > 0 && mStatus == STATUS_DEFAULT) {
                        setStatus(STATUS_SWIPING_TO_REFRESH);
                        mHeaderHander.onDropAnim(mRefreshHeaderView, dy);
//                        mRefreshTrigger.onStart(false, refreshHeaderViewHeight, mRefreshFinalMoveOffset);
                    } else if (dy < 0) {

                        if (mStatus == STATUS_SWIPING_TO_REFRESH && refreshHeaderContainerHeight <= 0) {
                            setStatus(STATUS_DEFAULT);
                            break;
                        }
                    }

                    if (mStatus == STATUS_SWIPING_TO_REFRESH || mStatus == STATUS_RELEASE_TO_REFRESH) {
                        if (refreshHeaderContainerHeight >= refreshHeaderViewHeight) {//两种状态切换
                            setStatus(STATUS_RELEASE_TO_REFRESH);
                        } else {
                            setStatus(STATUS_SWIPING_TO_REFRESH);
                        }

                        boolean upORdown = refreshHeaderContainerHeight <= refreshHeaderViewHeight;
                        mHeaderHander.onLimitDes(mRefreshHeaderView, upORdown, this);
                    }

//                    e.setAction(MotionEvent.ACTION_CANCEL);

                    fingerMove(dy);//TODO 移出来，让刷新的时候也可以拉动
                    super.onTouchEvent(e);
                    return true;
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
                System.out.println("EasyRecyclerView MotionEventCompat.ACTION_POINTER_UP=" + 1111);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                needResetAnim = true;      //松开的时候打开回弹
                System.out.println("EasyRecyclerView MotionEventCompat.ACTION_UP=" + 222);
                System.out.println("EasyRecyclerView getFirstVisiblePosition()=" + getFirstVisiblePosition());
//                final boolean triggerCondition = isEnabled() && mRefreshEnabled && mRefreshHeaderView != null && isFingerDragging() && canTriggerRefresh();
//                if (triggerCondition) {//是否是在临界点，也就是可以下来的位置
//                    onFingerUpStartAnimating();
//                }
                if (getFirstVisiblePosition() == 0) {
                    onFingerUpStartAnimating();
                    return true;
                    // invoke refresh
//                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
//                        startRefresh();
//                    }
//                    resetHeaderHeight();
                }
//                else if (getLastVisiblePosition() == mTotalItemCount - 1) {
//                    // invoke load more.
//                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
//                        startLoadMore();
//                    }
//                    resetFooterHeight();
//                }


                break;
        }
        return super.onTouchEvent(e);
    }

    private int getFirstVisiblePosition() {
//        switch (){
//            default:
//        }
        //getLayoutManager().getChildAt(0) 第一个显示的item
        int firstVisiblePosition = ((RecyclerView.LayoutParams) getLayoutManager().getChildAt(0).getLayoutParams()).getViewLayoutPosition();
        return firstVisiblePosition;
    }

    private boolean isFingerDragging() {
        return getScrollState() == SCROLL_STATE_DRAGGING;
    }

    public boolean canTriggerRefresh() {
        final Adapter adapter = getAdapter();
        if (adapter == null || adapter.getItemCount() <= 0) {
            return true;
        }
        View firstChild = getChildAt(0);
        int position = getChildLayoutPosition(firstChild);
        if (position == 0) {
            if (firstChild.getTop() == mRefreshHeaderContainer.getTop()) {
                return true;
            }
        }
        return false;
    }

    private int getMotionEventX(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getX(e, pointerIndex) + 0.5f);
    }

    private int getMotionEventY(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getY(e, pointerIndex) + 0.5f);
    }

    /**
     * 移动操作
     *
     * @param dy 移动的偏移量
     */
    private void fingerMove(int dy) {
        int ratioDy = (int) (dy / 2);//减速
//        int offset = mRefreshHeaderContainer.getMeasuredHeight();
//        int finalDragOffset = mRefreshFinalMoveOffset;
//
//        int nextOffset = offset + ratioDy;
//        if (finalDragOffset > 0) {
//            if (nextOffset > finalDragOffset) {
//                ratioDy = finalDragOffset - offset;
//            }
//        }
//
//        if (nextOffset < 0) {
//            ratioDy = -offset;
//        }
//        int movedx=0;
//        if (dy > 0) {
//            movedx = (int) ((float) ((600 + getScrollY()) / (float) 600) * dy / 1.6);
//        } else {
////                movedx= (int) dy;
//            movedx = (int) ((float) ((600 - getScrollY()) / (float) 600) * dy / 1.6);
//        }
        doMove(ratioDy);
    }

    /**
     * 移动操作
     *
     * @param dy
     */
    private void doMove(int dy) {
        if (dy != 0) {
            int height = mRefreshHeaderContainer.getHeight() + dy;//改变header高度
//            System.out.println("EasyRecyclerView mRefreshHeaderContainer.getHeight() =" + height);
            setRefreshHeaderContainerHeight(height);
//            mRefreshTrigger.onMove(false, false, height);//回调 TODO
        }
    }

    @Override
    public int computeVerticalScrollOffset() {
        int overScrollDistance = 0;

//        overScrollDistance = mScroller.getCurrY();
//        System.out.println("EasyRecyclerView  mScroller.getCurrY() =" + mScroller.getCurrY());
//        getLayoutManager().
        return super.computeVerticalScrollOffset() - overScrollDistance;
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    /**
     * 改变header的高度
     *
     * @param height
     */
    private void setRefreshHeaderContainerHeight(int height) {
//        mRefreshHeaderContainer.getLayoutParams().height = height;
//        mRefreshHeaderContainer.requestLayout();
        if (height < 0) {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mRefreshHeaderContainer.getLayoutParams();
        lp.height = height;
        mRefreshHeaderContainer.setLayoutParams(lp);
        getLayoutManager().scrollToPosition(0);//让回滚的时候先让header缩回去
//        System.out.println("EasyRecyclerView setRefreshHeaderContainerHeight height=" + height);
    }

    private void startScrollDefaultStatusToRefreshingStatus() {
//        mRefreshTrigger.onStart(true, mRefreshHeaderView.getMeasuredHeight(), mRefreshFinalMoveOffset);
        System.out.println("EasyRecyclerView startScrollRefreshingStatusToDefaultStatus=" + 1111);
        int targetHeight = mRefreshHeaderView.getMeasuredHeight();
        int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        mScroller.startScroll(0, mRefreshHeaderContainer.getHeight(), 0, mHeaderViewHeight, 200);
//        startScrollAnimation(400, new AccelerateInterpolator(), currentHeight, targetHeight);


    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= 1;
//        flin
        return super.fling(velocityX, velocityY);
//        return false;
    }

    /**
     * 回到关闭状态
     */
    private void startScrollSwipingToRefreshStatusToDefaultStatus() {
        final int targetHeight = 0;
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        System.out.println("EasyRecyclerView currentHeight=" + currentHeight);
        System.out.println("EasyRecyclerView currentHeight getHeight=" + mRefreshHeaderContainer.getBottom());
        System.out.println("EasyRecyclerView  startScroll start");
//        startScrollAnimation(300, new DecelerateInterpolator(), currentHeight, targetHeight);
        setStatus(STATUS_DEFAULT);
//        mScroller.a
        mScroller.startScroll(0, currentHeight, 0, -currentHeight, 200);
        System.out.println("EasyRecyclerView  startScroll end");
//        stopNestedScroll();
//        smoothToPosition(1);
//        smoothScrollToPosition(10);
//        postInvalidate();
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 复位到正在刷新 的位置
     */
    private void startScrollReleaseStatusToRefreshingStatus() {
//        mRefreshTrigger.onRelease();
        System.out.println("EasyRecyclerView startScrollRefreshingStatusToDefaultStatus=" + 3333);
        final int targetHeight = mRefreshHeaderView.getMeasuredHeight();
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
//        startScrollAnimation(300, new DecelerateInterpolator(), currentHeight, targetHeight);
        mScroller.startScroll(0, currentHeight, 0, mHeaderViewHeight - currentHeight, 200);

        invalidate();
        mHeaderHander.onStartAnim();
        refresh();

//        System.out.println("IRecyclerView startScrollReleaseStatusToRefreshingStatus=" + 2222);
//        System.out.println("IRecyclerView currentHeight=" + currentHeight + " ---  targetHeight=" + targetHeight);
    }

    boolean refreshIng;
    private OnRefreshListener listener;         //监听回调

    //正在刷新时候就不再进行第二次回调
    void refresh() {
        if (!isRefreshIng()) {
            refreshIng = true;//标记正在刷新
//            System.out.println("SpringView refreshIng=" + refreshIng);
            if (listener != null) {
                listener.onRefresh();
            }
        }

    }

    /**
     * 重置控件位置，暴露给外部的方法，用于在刷新或者加载完成后调用
     */
    public void onFinishFreshAndLoad() {
        setStatus(STATUS_COMPLETE);//完成标识
        System.out.println("cgp=needResetAnim="+needResetAnim);
        if (getFirstVisiblePosition() == 0&&needResetAnim) {
            startScrollSwipingToRefreshStatusToDefaultStatus();
        }
        refreshIng = false;
        if (mHeaderHander != null) mHeaderHander.onFinishAnim();

    }

    public void setListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    private void startScrollRefreshingStatusToDefaultStatus() {
//        mRefreshTrigger.onComplete();

        final int targetHeight = 0;
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
//        startScrollAnimation(400, new DecelerateInterpolator(), currentHeight, targetHeight);

        mScroller.startScroll(0, mRefreshHeaderContainer.getHeight(), 0, 0, 200);

        System.out.println("EasyRecyclerView startScrollRefreshingStatusToDefaultStatus=" + 44444444);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
//            scrollTo(0, mScroller.getCurrY()- mRefreshHeaderContainer.getHeight());
//            if (mScrollBack == SCROLLBACK_HEADER) {
//                mHeaderView.setVisiableHeight(mScroller.getCurrY());
//            int height = mRefreshHeaderContainer.getHeight() + mScroller.getCurrY();//改变header高度
//            System.out.println("IRecyclerView mScroller.getCurrY()="+mScroller.getCurrY());
            setRefreshHeaderContainerHeight(mScroller.getCurrY());
//            System.out.println("EasyRecyclerView mScroller.getCurrY()=" + mScroller.getCurrY());
//            } else {
//                mFooterView.setBottomMargin(mScroller.getCurrY());
//            }
//            postInvalidate();
//            invokeOnScrolling();
        }
        super.computeScroll();
    }

    private void onFingerUpStartAnimating() {
        System.out.println("EasyRecyclerView mStatus=" + mStatus);
        if (mStatus == STATUS_RELEASE_TO_REFRESH) {
            startScrollReleaseStatusToRefreshingStatus();
        } else if (needResetAnim && (mStatus == STATUS_SWIPING_TO_REFRESH || mStatus == STATUS_COMPLETE||mStatus == STATUS_DEFAULT)) {
            startScrollSwipingToRefreshStatusToDefaultStatus();
        }
    }

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

    private void setStatus(int status) {
        this.mStatus = status;
    }

    @Override
    public boolean isLoadIng() {
        return false;
    }

    @Override
    public boolean isRefreshIng() {
        return refreshIng;
    }

    public interface DragHander {
        View getView();

        int getDragLimitHeight(View rootView);

        int getDragMaxHeight(View rootView);

        int getDragSpringHeight(View rootView);

        void onPreDrag(View rootView);

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
