package cc.easyandroid.easyrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
public class EasyRecyclerView extends RecyclerView {
    private static final String TAG = EasyRecyclerView.class.getSimpleName();

    private static final int STATUS_DEFAULT = 0;

    private static final int STATUS_SWIPING_TO_REFRESH = 1;

    private static final int STATUS_RELEASE_TO_REFRESH = 2;

    private static final int STATUS_REFRESHING = 3;

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
    }

    public void setRefreshHeaderView(@LayoutRes int refreshHeaderLayoutRes) {
        ensureRefreshHeaderContainer();
        final View refreshHeader = LayoutInflater.from(getContext()).inflate(refreshHeaderLayoutRes, mRefreshHeaderContainer, false);
        if (refreshHeader != null) {
            setRefreshHeaderView(refreshHeader);
        }
    }

    public void setHeader(DragHander headerHander) {
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
            textView.setBackgroundColor(Color.BLACK);
            textView.setText("测试");
            textView.setHeight(100);
            textView.setTextSize(100);
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
                final boolean triggerCondition = isEnabled() && mRefreshEnabled && mRefreshHeaderView != null && isFingerDragging() && canTriggerRefresh();
                if (DEBUG) {
                    Log.i(TAG, "triggerCondition = " + triggerCondition + "; mStatus = " + mStatus + "; dy = " + dy);
                }
                if (triggerCondition) {//是否是在临界点，也就是可以下来的位置

                    final int refreshHeaderContainerHeight = mRefreshHeaderContainer.getMeasuredHeight();
                    final int refreshHeaderViewHeight = mRefreshHeaderView.getMeasuredHeight();

                    if (dy > 0 && mStatus == STATUS_DEFAULT) {
                        setStatus(STATUS_SWIPING_TO_REFRESH);
//                        mRefreshTrigger.onStart(false, refreshHeaderViewHeight, mRefreshFinalMoveOffset);
                    } else if (dy < 0) {
                        if (mStatus == STATUS_SWIPING_TO_REFRESH && refreshHeaderContainerHeight <= 0) {
                            setStatus(STATUS_DEFAULT);
                        }
                        if (mStatus == STATUS_DEFAULT) {
                            break;
                        }
                    }

                    if (mStatus == STATUS_SWIPING_TO_REFRESH || mStatus == STATUS_RELEASE_TO_REFRESH) {
                        if (refreshHeaderContainerHeight >= refreshHeaderViewHeight) {
                            setStatus(STATUS_RELEASE_TO_REFRESH);
                        } else {
                            setStatus(STATUS_SWIPING_TO_REFRESH);
                        }


                    }
                    fingerMove(dy);//TODO 移出来，让刷新的时候也可以拉动
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

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final boolean triggerCondition = isEnabled() && mRefreshEnabled && mRefreshHeaderView != null && isFingerDragging() && canTriggerRefresh();
                if (triggerCondition) {//是否是在临界点，也就是可以下来的位置
                    onFingerUpStartAnimating();
                }

                break;
        }
        return super.onTouchEvent(e);
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
        int ratioDy = (int) (dy * 0.5f + 0.5);//减速
        int offset = mRefreshHeaderContainer.getMeasuredHeight();
        int finalDragOffset = mRefreshFinalMoveOffset;

        int nextOffset = offset + ratioDy;
        if (finalDragOffset > 0) {
            if (nextOffset > finalDragOffset) {
                ratioDy = finalDragOffset - offset;
            }
        }

        if (nextOffset < 0) {
            ratioDy = -offset;
        }
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
            setRefreshHeaderContainerHeight(height);
//            mRefreshTrigger.onMove(false, false, height);//回调 TODO
        }
    }

    /**
     * 改变header的高度
     *
     * @param height
     */
    private void setRefreshHeaderContainerHeight(int height) {
        mRefreshHeaderContainer.getLayoutParams().height = height;
        mRefreshHeaderContainer.requestLayout();
//        System.out.println("EasyRecyclerView setRefreshHeaderContainerHeight height=" + height);
    }

    private void startScrollDefaultStatusToRefreshingStatus() {
//        mRefreshTrigger.onStart(true, mRefreshHeaderView.getMeasuredHeight(), mRefreshFinalMoveOffset);
        System.out.println("EasyRecyclerView startScrollRefreshingStatusToDefaultStatus=" + 1111);
        int targetHeight = mRefreshHeaderView.getMeasuredHeight();
        int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        mScroller.startScroll(0, mRefreshHeaderContainer.getHeight(), 0, mRefreshHeaderView.getHeight(), 400);
//        startScrollAnimation(400, new AccelerateInterpolator(), currentHeight, targetHeight);
    }

    private void startScrollSwipingToRefreshStatusToDefaultStatus() {
        final int targetHeight = 0;
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        System.out.println("EasyRecyclerView currentHeight=" + currentHeight);
        System.out.println("EasyRecyclerView currentHeight getHeight=" +  mRefreshHeaderContainer.getY());
//        startScrollAnimation(300, new DecelerateInterpolator(), currentHeight, targetHeight);
        mScroller.startScroll(0, currentHeight, 0, -currentHeight, 400);

    }

    /**
     * 复位到header =0 的位置
     */
    private void startScrollReleaseStatusToRefreshingStatus() {
//        mRefreshTrigger.onRelease();
        System.out.println("EasyRecyclerView startScrollRefreshingStatusToDefaultStatus=" + 3333);
        final int targetHeight = mRefreshHeaderView.getMeasuredHeight();
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
//        startScrollAnimation(300, new DecelerateInterpolator(), currentHeight, targetHeight);
        mScroller.startScroll(0, currentHeight, 0, targetHeight - currentHeight, 400);
//        System.out.println("IRecyclerView startScrollReleaseStatusToRefreshingStatus=" + 2222);
//        System.out.println("IRecyclerView currentHeight=" + currentHeight + " ---  targetHeight=" + targetHeight);
    }

    private void startScrollRefreshingStatusToDefaultStatus() {
//        mRefreshTrigger.onComplete();

        final int targetHeight = 0;
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
//        startScrollAnimation(400, new DecelerateInterpolator(), currentHeight, targetHeight);

        mScroller.startScroll(0, mRefreshHeaderContainer.getHeight(), 0, 0, 400);

        System.out.println("EasyRecyclerView startScrollRefreshingStatusToDefaultStatus=" + 44444444);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
//            if (mScrollBack == SCROLLBACK_HEADER) {
//                mHeaderView.setVisiableHeight(mScroller.getCurrY());
//            int height = mRefreshHeaderContainer.getHeight() + mScroller.getCurrY();//改变header高度
//            System.out.println("IRecyclerView mScroller.getCurrY()="+mScroller.getCurrY());
            setRefreshHeaderContainerHeight(mScroller.getCurrY());
//            System.out.println("EasyRecyclerView mScroller.getCurrY()=" + mScroller.getCurrY());
//            } else {
//                mFooterView.setBottomMargin(mScroller.getCurrY());
//            }
            postInvalidate();
//            invokeOnScrolling();
        }
        super.computeScroll();
    }

    private void onFingerUpStartAnimating() {
        if (mStatus == STATUS_RELEASE_TO_REFRESH) {
            startScrollReleaseStatusToRefreshingStatus();
        } else if (mStatus == STATUS_SWIPING_TO_REFRESH) {
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
        if (DEBUG) {
//            printStatusLog();
        }
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
