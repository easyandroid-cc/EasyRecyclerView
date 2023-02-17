package cc.easyandroid.easyrecyclerview.core

import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cc.easyandroid.easyrecyclerview.R
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener

/**
 */
class ProgressEmptyView(view: View, attrs: AttributeSet?, defStyleAttr: Int) : IProgressHander {
    private var mLoadingView: View? = null
    private var mEmptyView: View? = null
    private var mErrorView: View? = null
    private var mEmptyContainer: ViewGroup? = null
    private var mOnEasyProgressClickListener: OnEasyProgressClickListener? = null

    private fun setupLoadingView() {
        mLoadingView?.let {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            mEmptyContainer!!.addView(it)
            val clickView = it.findViewById(R.id.progressCanClickView) ?: it
            clickView.setOnClickListener {
                if (mOnEasyProgressClickListener != null) {
                    mOnEasyProgressClickListener!!.onLoadingViewClick()
                }
            }
            it.visibility = View.VISIBLE//默认第一次显示loadingView
        }
    }

    private fun setupErrorView() {
        mErrorView?.let {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            mEmptyContainer!!.addView(it)
            val clickView = it.findViewById(R.id.progressCanClickView) ?: it
            clickView.setOnClickListener {
                if (mOnEasyProgressClickListener != null) {
                    mOnEasyProgressClickListener!!.onErrorViewClick()
                }
            }
            it.visibility = View.GONE
        }
    }

    private fun setupEmtpyView() {
        mEmptyView?.let {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            mEmptyContainer!!.addView(it)
            val clickView = it.findViewById(R.id.progressCanClickView) ?: it
            clickView.setOnClickListener {
                if (mOnEasyProgressClickListener != null) {
                    mOnEasyProgressClickListener!!.onEmptyViewClick()
                }
            }
            it.visibility = View.GONE
        }
    }

    interface State {
        companion object {
            const val LOADING = 0
            const val EMPTY = 1
            const val ERROR = 2
        }
    }

    override fun getView(): View {
        return mEmptyContainer!!
    }

    override fun showEmptyView(message: String?) {
        showView(State.EMPTY, message)
    }

    override fun showErrorView(message: String?) {
        showView(State.ERROR, message)
    }

    override fun showLoadingView(message: String?) {
        showView(State.LOADING, message)
    }

    fun showView(state: Int, message: String?) {
        var showLoadingView = false
        var showEmptyView = false
        var showErrorView = false
        when (state) {
            State.LOADING -> showLoadingView = true
            State.EMPTY -> showEmptyView = true
            State.ERROR -> showErrorView = true
        }
        mLoadingView?.let {
            it.visibility = if (showLoadingView) View.VISIBLE else View.GONE
            if (!TextUtils.isEmpty(message) && showLoadingView) {
                val loadingTextView =
                    it.findViewById<TextView>(R.id.progressMessageTextView)
                if (loadingTextView != null) {
                    loadingTextView.text = message
                }
            }
        }

        mEmptyView?.let {
            it.visibility = if (showEmptyView) View.VISIBLE else View.GONE
            if (!TextUtils.isEmpty(message) && showEmptyView) {
                val emptyViewTextView =
                    it.findViewById<TextView>(R.id.progressMessageTextView)
                if (emptyViewTextView != null) {
                    emptyViewTextView.text = message
                }
            }
        }

        mErrorView?.let {
            it.visibility = if (showErrorView) View.VISIBLE else View.GONE
            if (!TextUtils.isEmpty(message) && showErrorView) {
                val errorViewTextView =
                    it.findViewById<TextView>(R.id.progressMessageTextView)
                if (errorViewTextView != null) {
                    errorViewTextView.text = message
                }
            }
        }

    }

    override fun setOnEasyProgressClickListener(listener: OnEasyProgressClickListener) {
        mOnEasyProgressClickListener = listener
    }

    init {
        val mLayoutInflater = LayoutInflater.from(view.context)
        val a = view.context.theme.obtainStyledAttributes(attrs,
            R.styleable.ProgressEmptyView,
            defStyleAttr,
            0)
        a.getResourceId(R.styleable.ProgressEmptyView_easyLoadingView,
            R.layout.easyloadingview).let {
            mLoadingView = mLayoutInflater.inflate(it, null)
        } // 正在加载的view
        a.getResourceId(R.styleable.ProgressEmptyView_easyEmptyView,
            R.layout.easyemptyview).let {
            mEmptyView = mLayoutInflater.inflate(it, null)
        } // 空数据的view
        a.getResourceId(R.styleable.ProgressEmptyView_easyErrorView,
            R.layout.easyerrorview).let {
            mErrorView = mLayoutInflater.inflate(it, null)
        } // 错误的view
        val easyEmptyContainerId = a.getResourceId(R.styleable.ProgressEmptyView_easyEmptyContainer,
            R.layout.easyemptycontainer) // view容器


        val viewGroup = view.parent?.run {
            this as ViewGroup
        }
        mEmptyContainer =//父容器中如果有easyEmptyContainerId 优先使用
            viewGroup?.findViewById(easyEmptyContainerId) ?: mLayoutInflater.inflate(
                easyEmptyContainerId,
                null) as ViewGroup
        mEmptyContainer?.let {
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            it.visibility = View.GONE
        }
        a.recycle()

        setupEmtpyView()
        setupErrorView()
        setupLoadingView()
    }
}