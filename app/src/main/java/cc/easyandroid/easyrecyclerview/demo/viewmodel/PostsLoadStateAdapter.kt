package cc.easyandroid.easyrecyclerview.demo.viewmodel

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import cc.easyandroid.easyrecyclerview.EasyRecyclerView
import cc.easyandroid.easyrecyclerview.items.IFlexible
import kotlinx.coroutines.handleCoroutineException

class PostsLoadStateAdapter(
        private val adapter: PostsAdapter
) : LoadStateAdapter<NetworkStateItemViewHolder>() {
    init {
    }

    override fun onBindViewHolder(holder: NetworkStateItemViewHolder, loadState1: LoadState) {
        //println("cgp4 onBindViewHolder")
        holder.bindTo(loadState1)

    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            loadState: LoadState
    ): NetworkStateItemViewHolder {
        //println("cgp4 onCreateViewHolder")
        println("cgp mRecyclerView.mRefreshHeaderContainer 11111")
        return NetworkStateItemViewHolder(parent) {
            adapter.retry()
        }
    }

//    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
//        return  true
//    }


}