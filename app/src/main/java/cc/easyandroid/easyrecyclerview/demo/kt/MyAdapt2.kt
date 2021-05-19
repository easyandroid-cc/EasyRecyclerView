package cc.easyandroid.easyrecyclerview.demo.kt

import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import androidx.paging.*
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter
import cc.easyandroid.easyrecyclerview.demo.viewmodel.IFHolder_Kt
import cc.easyandroid.easyrecyclerview.demo.viewmodel.PostsAdapter
import cc.easyandroid.easyrecyclerview.items.IFlexible
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow


open class MyAdapt2<T : IFlexible<*>>(diffCallback: DiffUtil.ItemCallback<T>) : EasyFlexibleAdapter<T>() {

    private val innerAdapter: MyAdapt<T> = MyAdapt(diffCallback)


    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItem(position: Int): T? {
        return innerAdapter.getItem1(position = position)
    }

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        innerAdapter.addLoadStateListener(listener)
    }

    suspend fun submitData(pagingData: PagingData<T>) {
        innerAdapter.submitData(pagingData)
    }

    fun refresh() {
        innerAdapter.refresh()
    }
    fun retry() {
        innerAdapter.retry()
    }

    fun withLoadStateHeader(
            header: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
        }
        return innerAdapter.withLoadStateHeader(header)
    }

    /**
     * Create a [ConcatAdapter] with the provided [LoadStateAdapter]s displaying the
     * [LoadType.APPEND] [LoadState] as a list item at the start of the presented list.
     *
     * @see LoadStateAdapter
     * @see withLoadStateHeaderAndFooter
     * @see withLoadStateHeader
     */
    fun withLoadStateFooter(
            footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            footer.loadState = loadStates.append
        }
        return innerAdapter.withLoadStateFooter(footer)
    }

    /**
     * Create a [ConcatAdapter] with the provided [LoadStateAdapter]s displaying the
     * [LoadType.PREPEND] and [LoadType.APPEND] [LoadState]s as list items at the start and end
     * respectively.
     *
     * @see LoadStateAdapter
     * @see withLoadStateHeader
     * @see withLoadStateFooter
     */
    fun withLoadStateHeaderAndFooter(
            header: LoadStateAdapter<*>,
            footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
            footer.loadState = loadStates.append
        }
        return innerAdapter.withLoadStateHeaderAndFooter(header, footer)
    }

    fun submitData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        innerAdapter.submitData(lifecycle, pagingData)
    }

    /**
     * Remove a previously registered [CombinedLoadStates] listener.
     *
     * @param listener Previously registered listener.
     * @see addLoadStateListener
     */
    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        innerAdapter.removeLoadStateListener(listener)
    }


    inner class MyAdapt<T : IFlexible<*>>(diffCallback: DiffUtil.ItemCallback<T>, mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
                                         workerDispatcher: CoroutineDispatcher = Dispatchers.Default) : PagingDataAdapter<T, RecyclerView.ViewHolder>(diffCallback, mainDispatcher, workerDispatcher) {


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return super@MyAdapt2.onBindViewHolder(holder, position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return super@MyAdapt2.onCreateViewHolder(parent, viewType)
        }


        public fun getItem1(@androidx.annotation.IntRange position: kotlin.Int): T? {
            return getItem(position)
        }

    }
}