package cc.easyandroid.easyrecyclerview.demo.viewmodel

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import cc.easyandroid.easyrecyclerview.EasyPullRefreshRecyclerView
import cc.easyandroid.easyrecyclerview.core.DefaultHeaderHander

class RefreshAdapter(val myPagingAdapter: PostsAdapter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        myPagingAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                this@RefreshAdapter.notifyDataSetChanged()
            }
        })
    }

    lateinit var mRecyclerView: EasyPullRefreshRecyclerView
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView as EasyPullRefreshRecyclerView
        mRecyclerView.headerHander = DefaultHeaderHander(mRecyclerView.context)
        //println("cgp onAttachedToRecyclerView")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(mRecyclerView.refreshHeaderContainer) {}
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // TODO("Not yet implemented")
    }
}
