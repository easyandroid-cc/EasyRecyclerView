package cc.easyandroid.easyrecyclerview.demo.kt

import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter
import cc.easyandroid.easyrecyclerview.items.IFlexible
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class MyAdapt<T : IFlexible<*>>(diffCallback: DiffUtil.ItemCallback<T>, mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
                                     workerDispatcher: CoroutineDispatcher = Dispatchers.Default) : PagingDataAdapter<T, RecyclerView.ViewHolder>(diffCallback, mainDispatcher, workerDispatcher) {

    private val innerAdapter: EasyFlexibleAdapter<T> = EasyFlexibleAdapter {

        fun getItem(position: Int): IFlexible<*>? {
            //return super.getItem(position)
              return this@MyAdapt.getItem(position)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

//    fun EasyFlexibleAdapter<T>.getItem(@IntRange(from = 0) position: Int): T? {
//        return this@MyAdapt.getItem(position)
//        // return differ.getItem(position)
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        innerAdapter.onBindViewHolder(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return innerAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return innerAdapter.getItemViewType(position)
        // return super.getItemViewType(position)
    }

}