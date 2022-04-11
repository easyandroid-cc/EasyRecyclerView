package cc.easyandroid.easyrecyclerview.demo.viewmodel

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import cc.easyandroid.easyrecyclerview.EasyPullRefreshRecyclerView
import cc.easyandroid.easyrecyclerview.core.DefaultHeaderHander
import java.lang.IllegalArgumentException

class RefreshAdapter2( ) : LoadStateAdapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState1: LoadState) {
        //  not use
    }
    /**
     * 这里的parent 也是RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): RecyclerView.ViewHolder {
        return singleViewHolder(parent)
    }

    private lateinit var viewHolder: RecyclerView.ViewHolder// 这里必须是单一
    private fun singleViewHolder(view: ViewGroup): RecyclerView.ViewHolder {
        if (!this::viewHolder.isInitialized && view is EasyPullRefreshRecyclerView) {
            viewHolder= object : RecyclerView.ViewHolder(view.refreshHeaderContainer) {}
            view.headerHander = DefaultHeaderHander(view.context)
        }
        return viewHolder
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        if(loadState is LoadState.Error){
           loadState.error.message
        }
        return loadState is LoadState.Loading || loadState is LoadState.Error || loadState.endOfPaginationReached
    }

}
