/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.easyandroid.easyrecyclerview.demo.viewmodel

import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.LoadType
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import com.house101.app1.kt.adapter.EasyPagingDataAdapter

/**
 * A simple adapter implementation that shows Reddit posts.
 */
class PostsAdapter()
    : EasyPagingDataAdapter<IFHolder_Kt>(POST_COMPARATOR) {

//    override fun onBindViewHolder(holder: RedditPostViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    override fun onBindViewHolder(
//            holder: RedditPostViewHolder,
//            position: Int,
//            payloads: MutableList<Any>
//    ) {
//        if (payloads.isNotEmpty()) {
//            val item = getItem(position)
//            holder.updateScore(item)
//        } else {
//            onBindViewHolder(holder, position)
//        }
//    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedditPostViewHolder {
//        return RedditPostViewHolder.create(parent)
//    }

    companion object {
        private val PAYLOAD_SCORE = Any()
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<IFHolder_Kt>() {
            override fun areContentsTheSame(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Boolean =
                    oldItem .equals( newItem)

            override fun areItemsTheSame(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Boolean =
                   // oldItem.name == newItem.name
                    false

            override fun getChangePayload(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Any? {
                return if (sameExceptScore(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptScore(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Boolean {
            // DON'T do this copy in a real app, it is just convenient here for the demo :)
            // because reddit randomizes scores, we want to pass it as a payload to minimize
            // UI updates between refreshes
            return false
//            return oldItem.copy(score = newItem.score) == newItem
        }
    }
    fun withLoadStateHeader1(
            header: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
        }
        return ConcatAdapter(header, this)
    }

    /**
     * Create a [ConcatAdapter] with the provided [LoadStateAdapter]s displaying the
     * [LoadType.APPEND] [LoadState] as a list item at the start of the presented list.
     *
     * @see LoadStateAdapter
     * @see withLoadStateHeaderAndFooter
     * @see withLoadStateHeader
     */
    fun withLoadStateFooter1(
            footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(this, footer)
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
     fun withLoadStateHeaderAndFooter1(
            header: LoadStateAdapter<*>,
            footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            header.loadState = loadStates.prepend
            footer.loadState = loadStates.append
        }
        return ConcatAdapter(header, this, footer)
    }


}
