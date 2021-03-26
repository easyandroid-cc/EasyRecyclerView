package cc.easyandroid.easyrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.ArrayList;

import cc.easyandroid.easyrecyclerview.core.IEasyAdapter;
import cc.easyandroid.easyrecyclerview.core.IStateAdapter;
import cc.easyandroid.easyrecyclerview.core.PullViewHandle;
import cc.easyandroid.easyrecyclerview.core.RefreshHeaderLayout;
import cc.easyandroid.easyrecyclerview.listener.OnLoadMoreListener;
import cc.easyandroid.easyrecyclerview.listener.OnRefreshListener;

/**
 * 下拉刷新
 */
public class EasyRecyclerView extends EasyProgressRecyclerView implements PullViewHandle {
    private static final String TAG = EasyRecyclerView.class.getSimpleName();

    private static final int STATUS_DEFAULT = 0;//默认

    private static final int STATUS_SWIPING_TO_REFRESH = 1;//下拉刷新

    private static final int STATUS_RELEASE_TO_REFRESH = 2;//松开加载

    private static final int STATUS_REFRESHING = 3;//正在刷新状态

    private static final int STATUS_COMPLETE = 4;//刷新或者加载完成的状态

    private int mStatus = STATUS_DEFAULT;//当前的状态

    private boolean mRefreshEnabled = false;

    private boolean mLoadMoreEnabled;

    private boolean mAutoLoadMore = true;

    private OnRefreshListener mOnRefreshListener;

    private OnLoadMoreListener mOnLoadMoreListener;

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

    private boolean refreshIng;//是否在刷新中

    private boolean loadMoreIng;//是否在加载中

    private boolean firstMove;//headerview 第一次出现的时候需要

    private HeaderHander mHeaderHander;

    private FooterHander mFooterHander;

    private EasyOnScrollListener easyOnScrollListener;

    private final float resistance = 1.7f;

    private ArrayList<HeaderHeightChangedListener> mHeaderHeightChangedListeners;

    /**
     * 距离最后多少项时候开始自动加载
     */
    private int restItemCountToLoadMore = 0;

    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EasyRecyclerViewStyle);
    }


    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasyRecyclerView, defStyle, 0);
        boolean refreshEnabled;
        boolean loadMoreEnabled;
        boolean autoloadMore;
        try {
            refreshEnabled = a.getBoolean(R.styleable.EasyRecyclerView_refreshEnabled, true);
            loadMoreEnabled = a.getBoolean(R.styleable.EasyRecyclerView_loadMoreEnabled, true);
            autoloadMore = a.getBoolean(R.styleable.EasyRecyclerView_autoloadMore, true);

        } finally {
            a.recycle();
        }

        mScroller = new Scroller(context, new DecelerateInterpolator());
        easyOnScrollListener = new EasyOnScrollListener(this);
        setRefreshEnabled(refreshEnabled);
        setLoadMoreEnabled(loadMoreEnabled);
        setAutoLoadMore(autoloadMore);
        setStatus(STATUS_DEFAULT);//开始设置为默认
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = e.getActionMasked();
        final int actionIndex = e.getActionIndex();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
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
            case MotionEvent.ACTION_POINTER_UP: {
                onPointerUp(e);
            }
            break;
        }
        return super.onTouchEvent(e);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                firstMove = true;
                needResetAnim = false;      //按下的时候关闭回弹
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
                    break;
                }

                final int x = getMotionEventX(e, index);
                final int y = getMotionEventY(e, index);

                int dy = y - mLastTouchY;
                mLastTouchX = x;
                mLastTouchY = y;

                boolean triggerCondition = getFirstVisiblePosition() == 0 && isFingerDragging() && mRefreshEnabled;//第一个item显示

                if (triggerCondition && mHeaderHander != null) {//是否是在临界点，也就是可以下来的位置
                    return handlePull(e, dy);
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
//                getFirstVisiblePosition() == 0 &&
                post(new Runnable() {
                    public void run() {
                        if (mRefreshHeaderContainer.getHeight() > 0) {//抬起手指后复位
                            onFingerUpStartAnimating();
                        }
                    }
                });

                break;
        }
        return super.dispatchTouchEvent(e);
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

    private int getFirstVisiblePosition() {
        View firstView = getLayoutManager().getChildAt(0);
        if (firstView != null) {
            return ((RecyclerView.LayoutParams) getLayoutManager().getChildAt(0).getLayoutParams()).getViewLayoutPosition();
        }
        return 0;
    }

    public HeaderHander getHeaderHander() {
        return mHeaderHander;
    }

    public FooterHander getFooterHander() {
        return mFooterHander;
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
     *
     * @param e  事件e
     * @param dy 拖动距离
     * @return 是否自己处理
     */
    private boolean handlePull(MotionEvent e, int dy) {
        final int refreshHeaderContainerHeight = mRefreshHeaderContainer.getMeasuredHeight();
        final int refreshHeaderViewHeight = mHeaderViewHeight;
        mHeaderHander.onPreDrag(mRefreshHeaderView);
        if (dy > 0 && mStatus == STATUS_SWIPING_TO_REFRESH) {//下拉
            //Not used
        } else if (dy < 0) {//上拉
            if ((mStatus == STATUS_SWIPING_TO_REFRESH || mStatus == STATUS_COMPLETE || mStatus == STATUS_REFRESHING) && refreshHeaderContainerHeight <= 0) {//这几种状态要跳出，让至空间自己移动，不然会卡住
                if (mStatus == STATUS_COMPLETE) {//完成上拉 再下来一个是下拉刷新  //完成后再往上拉要恢复到默认状态
                    setStatus(STATUS_DEFAULT);
                }
                return super.dispatchTouchEvent(e);
            }
        }
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
        fingerMove(dy);//TODO  放在if 外面 让刷新的时候也可以拉动

        firstMove = false;
        return super.dispatchTouchEvent(e);
    }

    /**
     * 移动操作
     *
     * @param dy 移动的偏移量
     */
    private void fingerMove(int dy) {
        mHeaderHander.onDropAnim(mRefreshHeaderContainer, dy);
        int ratioDy;
        if (dy > 0) {//减速
            ratioDy = (int) (((mHeaderHander.getDragMaxHeight(this) - mRefreshHeaderContainer.getHeight()) / (float) mHeaderHander.getDragMaxHeight(this)) * dy / resistance);
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
//        if (mRefreshHeaderContainer.getLayoutParams().height != height) {
        mRefreshHeaderContainer.getLayoutParams().height = height;
        mRefreshHeaderContainer.requestLayout();
        if (mHeaderHeightChangedListeners != null) {
            for (int i = mHeaderHeightChangedListeners.size() - 1; i >= 0; i--) {
                mHeaderHeightChangedListeners.get(i).onChanged(height);
            }
        }
        getLayoutManager().scrollToPosition(0);//让回滚的时候先让header缩回去
//        }
    }

    /**
     * 回到关闭状态
     */
    private void startScrollSwipingToRefreshStatusToDefaultStatus() {
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        if (currentHeight == 0) {
            setStatus(STATUS_DEFAULT);
            return;
        }
        mScroller.startScroll(0, currentHeight, 0, -currentHeight, 120);
        postInvalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
        setStatus(STATUS_DEFAULT);
    }

    /**
     * 释放进入到刷新状态                  复位到正在刷新 的位置
     * autoRefresh 是否是autoRefresh，（手动刷新，当上一次还没有刷新完成时是不能进行新的刷新的，，如果是autoRefresh，会取消之前的刷新，进行新的刷新动作）
     */
    private void startScrollReleaseStatusToRefreshingStatus(boolean autoRefresh) {
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        mScroller.startScroll(0, currentHeight, 0, mHeaderViewHeight - currentHeight, 200);
        postInvalidate();
        setStatus(STATUS_REFRESHING);//设置当前状态是刷新状态
        refresh(autoRefresh);
    }

    /**
     * 正在刷新时候下拉的会弹
     */
    private void startScrollRefreshingStatusToRefreshingStatus() {
        final int currentHeight = mRefreshHeaderContainer.getMeasuredHeight();
        mScroller.startScroll(0, currentHeight, 0, mHeaderViewHeight - currentHeight, 200);
        postInvalidate();
    }

    void loadMore() {
        if (mLoadMoreEnabled && mFooterHander != null && mFooterHander.onCanLoadMore() && !isLoadIng()) {
            loadMoreIng = true;
            mFooterHander.showLoading();
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore(mFooterHander);
            }
        }
    }


    /**
     * 刷新
     *
     * @param autoRefresh true 如果正在进行刷新，也进入回调刷新，
     */
    void refresh(boolean autoRefresh) {
        if (!isRefreshIng() || autoRefresh) {
            refreshIng = true;//标记正在刷新
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onRefresh();
            }
            mHeaderHander.onStartAnim();
        }
    }

    boolean headerHanderIsNull() {
        return mHeaderHander == null;
    }

    /**
     * 自动刷新一定要回调刷新（手拉的不刷新如果正在刷新就不重复进入到刷新）
     */
    public void autoRefresh() {
        needResetAnim = true;
        mHeaderHander.onPreDrag(mRefreshHeaderView);
        startScrollReleaseStatusToRefreshingStatus(true);
    }

    /**
     * 重置控件位置，暴露给外部的方法，用于在刷新或者加载完成后调用
     */
    public void finishRefresh(boolean refreshSuccess) {
        if (getFirstVisiblePosition() == 0) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshIng = false;
                    if (getFirstVisiblePosition() == 0) {
                        setStatus(STATUS_COMPLETE);//完成标识
                        if (needResetAnim) {
                            startScrollSwipingToRefreshStatusToDefaultStatus();
                        }
                    } else {
                        setStatus(STATUS_DEFAULT);//完成标识
                    }
                }
            }, 500);
        } else {
            setStatus(STATUS_DEFAULT);//完成标识
            refreshIng = false;
        }

        if (mHeaderHander != null) mHeaderHander.onFinishAnim(refreshSuccess);

    }

    public void stopRefreshAndLoadMore() {
        setStatus(STATUS_DEFAULT);//完成标识
        refreshIng = false;
        loadMoreIng = false;

    }

    public void finishLoadMore(int loadstatus) {
        loadMoreIng = false;
        if (mFooterHander != null) {
            switch (loadstatus) {
                case FooterHander.LOADSTATUS_COMPLETED:
                    mFooterHander.showLoadCompleted();
                    break;
                case FooterHander.LOADSTATUS_FAIL:
                    mFooterHander.showLoadFail();
                    break;
                case FooterHander.LOADSTATUS_FULLCOMPLETED:
                    mFooterHander.showLoadFullCompleted();
                    break;
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            setRefreshHeaderContainerHeight(mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }


    //抬起手指后复位
    private void onFingerUpStartAnimating() {
        if (mStatus == STATUS_RELEASE_TO_REFRESH) {
            startScrollReleaseStatusToRefreshingStatus(false);
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
        if (mRefreshHeaderContainer != null) {
            mRefreshHeaderContainer.setVisibility(mRefreshEnabled ? View.VISIBLE : View.GONE);
            mRefreshHeaderContainer.getLayoutParams().height = mRefreshEnabled ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
            mRefreshHeaderContainer.requestLayout();
        }
    }

    public void setLoadMoreEnabled(boolean enabled) {
        this.mLoadMoreEnabled = enabled;
        if (mLoadMoreFooterContainer != null) {
            mLoadMoreFooterContainer.setVisibility(mLoadMoreEnabled ? View.VISIBLE : View.GONE);
            mLoadMoreFooterContainer.getLayoutParams().height = mLoadMoreEnabled ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
            mLoadMoreFooterContainer.requestLayout();
        }
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        mAutoLoadMore = autoLoadMore;
    }

    public boolean isAutoLoadMore() {
        return mAutoLoadMore;
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
        refreshHeaderView.setVisibility(mRefreshEnabled ? View.VISIBLE : View.GONE);
        mRefreshHeaderContainer.setVisibility(mRefreshEnabled ? View.VISIBLE : View.GONE);
    }


    public void setHeaderHander(HeaderHander headerHander) {
        mHeaderHander = headerHander;
        setRefreshHeaderView(headerHander.getView());
    }

    public void setFooterHander(FooterHander footerHander) {
        mFooterHander = footerHander;
        setLoadMoreFooterView(footerHander.getView());
    }

    public void addHeaderHeightChangedListener(HeaderHeightChangedListener headerHeightChangedListener) {
        if (mHeaderHeightChangedListeners == null) {
            mHeaderHeightChangedListeners = new ArrayList<>();
        }
        mHeaderHeightChangedListeners.add(headerHeightChangedListener);
    }

    public void removeHeaderHeightChangedListener(HeaderHeightChangedListener headerHeightChangedListener) {
        if (mHeaderHeightChangedListeners != null) {
            mHeaderHeightChangedListeners.remove(headerHeightChangedListener);
        }
    }

    public void clearHeaderHeightChangedListeners() {
        if (mHeaderHeightChangedListeners != null) {
            mHeaderHeightChangedListeners.clear();
        }
    }


    private void setLoadMoreFooterView(View loadMoreFooterView) {
        if (mLoadMoreFooterView != null) {
            removeLoadMoreFooterView();
        }
        if (mLoadMoreFooterView != loadMoreFooterView) {
            this.mLoadMoreFooterView = loadMoreFooterView;
            this.mLoadMoreFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFooterHander.onCanLoadMore()) {
                        loadMore();
                    }
                }
            });
            ensureLoadMoreFooterContainer();
            mLoadMoreFooterContainer.addView(loadMoreFooterView);
        }
    }

    public void setRestItemCountToLoadMore(int restItemCountToLoadMore) {
        this.restItemCountToLoadMore = restItemCountToLoadMore;
    }

    public int getRestItemCountToLoadMore() {
        return restItemCountToLoadMore;
    }

    public void setAdapter(Adapter adapter) {
        ensureRefreshHeaderContainer();
        ensureLoadMoreFooterContainer();
        if (adapter instanceof IEasyAdapter) {
            IEasyAdapter baseRecyclerAdapter = (IEasyAdapter) adapter;
            baseRecyclerAdapter.addHeaderViewToFirst(mRefreshHeaderContainer);
            baseRecyclerAdapter.addFooterViewToLast(mLoadMoreFooterContainer);
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
            mLoadMoreFooterContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mLoadMoreEnabled ? ViewGroup.LayoutParams.WRAP_CONTENT : 0));
            mLoadMoreFooterContainer.setVisibility(mLoadMoreEnabled ? View.VISIBLE : View.GONE);
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
        return loadMoreIng;
    }

    /**
     * 是否是手指按下起的第一次移动
     */

    @Override
    public boolean isFirstMove() {
        return firstMove;
    }

    @Override
    public boolean isRefreshIng() {
        return refreshIng;
    }

    /**
     * 滑动监听
     */
    private static class EasyOnScrollListener extends RecyclerView.OnScrollListener {
        private final EasyRecyclerView easyRecyclerView;

        public EasyOnScrollListener(EasyRecyclerView easyRecyclerView) {
            super();
            this.easyRecyclerView = easyRecyclerView;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //1.刷新时候滚动到底部不让自动加载 2.刷新前面
            if (newState == RecyclerView.SCROLL_STATE_IDLE && (isScollBottom(recyclerView) || canTriggerLoadMore(easyRecyclerView)) && !easyRecyclerView.isRefreshIng() && !isFirstItemVisible(easyRecyclerView)) {
                if (easyRecyclerView.isAutoLoadMore()) {
                    easyRecyclerView.loadMore();
                }
            }
        }

        /**
         * 是否滚动到了最底部
         *
         * @param recyclerView
         * @return boolean
         */
        private boolean isScollBottom(RecyclerView recyclerView) {
            return !isCanScollVertically(recyclerView);
        }

        /**
         * 列表中第一个item显示的时候也不让他自动加载
         *
         * @param recyclerView
         * @return
         */
        private boolean canTriggerLoadMore(EasyRecyclerView recyclerView) {
            View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);//recyclerView 中的最后一个item
            int position = recyclerView.getChildLayoutPosition(lastChild);//recyclerView 中的最后一个position
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();//全部item的个数
            return totalItemCount - recyclerView.getRestItemCountToLoadMore() <= position + 1;//滚动到最后一个了
        }

        /**
         * 第一个Item 是否显示，防止刷新的时候加载被调用
         * @param recyclerView
         * @return
         */
        private boolean isFirstItemVisible(EasyRecyclerView recyclerView) {
            View firstChild = recyclerView.getChildAt(0);//recyclerView 中的第一个item
            int position = recyclerView.getChildLayoutPosition(firstChild);//recyclerView 中的第一个itemposition
            return position == 0;//第一个Item 是否显示
        }

        private boolean isCanScollVertically(RecyclerView recyclerView) {
            if (android.os.Build.VERSION.SDK_INT < 14) {
                return ViewCompat.canScrollVertically(recyclerView, 1) || recyclerView.getScrollY() < recyclerView.getHeight();
            } else {
                return ViewCompat.canScrollVertically(recyclerView, 1);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnScrollListener(easyOnScrollListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnScrollListener(easyOnScrollListener);
    }


    public interface HeaderHander {
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

    public interface FooterHander {
        int LOADSTATUS_COMPLETED = 0, LOADSTATUS_FAIL = 1, LOADSTATUS_FULLCOMPLETED = 2;

        View getView();

        /**
         * 显示普通布局
         */
        void showLoadCompleted();

        /**
         * 显示已经加载完成，没有更多数据的布局
         */
        void showLoadFullCompleted();

        /**
         * 显示正在加载中的布局
         */
        void showLoading();

        /**
         * 显示加载失败的布局
         */
        void showLoadFail();

        boolean onCanLoadMore();
    }

    public interface HeaderHeightChangedListener {
        void onChanged(int headerHeight);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable("super_data"));
        Adapter adapter = getAdapter();
        if (adapter != null && adapter instanceof IStateAdapter) {
            IStateAdapter iStateAdapter = (IStateAdapter) adapter;//让adapter有恢复数据的功能
            iStateAdapter.onRestoreInstanceState(bundle);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super_data", super.onSaveInstanceState());
        Adapter adapter = getAdapter();
        if (adapter != null && adapter instanceof IStateAdapter) {
            IStateAdapter iStateAdapter = (IStateAdapter) adapter;//让adapter有保存
            iStateAdapter.onSaveInstanceState(bundle);
        }
        return bundle;
    }
}
