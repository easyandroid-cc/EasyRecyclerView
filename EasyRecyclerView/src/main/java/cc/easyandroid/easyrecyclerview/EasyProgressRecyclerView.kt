package cc.easyandroid.easyrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import kotlin.jvm.JvmOverloads
import cc.easyandroid.easyrecyclerview.R
import androidx.recyclerview.widget.RecyclerView
import cc.easyandroid.easyrecyclerview.core.IProgressHander
import androidx.recyclerview.widget.ConcatAdapter
import cc.easyandroid.easyrecyclerview.core.IEmptyAdapter
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener
import android.view.ViewGroup
import cc.easyandroid.easyrecyclerview.core.ProgressEmptyView
import java.lang.IllegalStateException

/**
 * EasyProgressRecyclerView
 */
open class EasyProgressRecyclerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.EasyRecyclerViewStyle,
) : RecyclerView(
    context!!, attrs, defStyle) {
    fun setProgressHander(progressHander: IProgressHander) {
        mProgressHander = progressHander
        emptyView = progressHander.view
    }

    private var mProgressHander: IProgressHander? = null
    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.let {//先取消注册
            if (it is IEmptyAdapter) {
                if (it.hasObservers()) {
                    it.unregisterAdapterDataObserver(emptyObserver)
                }
            } else if (it is ConcatAdapter) {
                val adapters = it.adapters
                adapters.forEach { childAdapter ->
                    if (childAdapter is IEmptyAdapter) {
                        if (childAdapter.hasObservers()) {
                            childAdapter.unregisterAdapterDataObserver(emptyObserver)
                        }
                    }
                }
            }
        }

        adapter?.let {//重新注册
            if (it is IEmptyAdapter) {
                it.registerAdapterDataObserver(emptyObserver)
            } else if (it is ConcatAdapter) {
                val adapters = it.adapters
                adapters.forEach { childAdapter ->
                    if (childAdapter is IEmptyAdapter) {
                        childAdapter.registerAdapterDataObserver(emptyObserver)
                    }
                }
            }
        }
        super.setAdapter(adapter)
        emptyObserver.onChanged()
    }

    @JvmOverloads
    fun showLoadingView(message: String? = null) {
        mProgressHander!!.showLoadingView(message)
    }

    @JvmOverloads
    fun showEmptyView(message: String? = null) {
        mProgressHander!!.showEmptyView(message)
    }

    @JvmOverloads
    fun showErrorView(message: String? = null) {
        mProgressHander!!.showErrorView(message)
    }

    fun setOnEasyProgressClickListener(listener: OnEasyProgressClickListener?) {
        mProgressHander!!.setOnEasyProgressClickListener(listener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 要添加在window后才能找到parent
        val parent = parent as ViewGroup
            ?: throw IllegalStateException(javaClass.simpleName + " is not attached to parent view.")
        if (emptyView != null) {
            parent.removeView(emptyView)
            parent.addView(emptyView)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    var emptyView: View? = null
    var emptyObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            updata()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            updata()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            updata()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            updata()
        }

        override fun onChanged() {
            updata()
        }
    }


    fun updata() {
        val adapter = adapter
        if (emptyView != null) {
            if (adapter is ConcatAdapter) {
                val adapters = adapter.adapters
                for (childAdapter in adapters) { //遍历子adapter 进行判断
                    if (handleEmptyAdapter(childAdapter)) return
                }
            } else {
                if (handleEmptyAdapter(adapter)) return
            }
        }
    }

    private fun handleEmptyAdapter(ada: Adapter<*>?): Boolean {
        if (ada is IEmptyAdapter) {
            val iEmptyAdapter = ada as IEmptyAdapter
            if (!iEmptyAdapter.isEmpty) {
                emptyView!!.visibility = GONE
                this@EasyProgressRecyclerView.visibility = VISIBLE
            } else {
                emptyView!!.visibility = VISIBLE
                this@EasyProgressRecyclerView.visibility = GONE
            }
            return true
        }
        return false
    }

    init {
        val progressEmptyView = ProgressEmptyView(this, attrs, defStyle)
        setProgressHander(progressEmptyView)
    }
}