///*
// * Copyright (C) 2017 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package cc.easyandroid.easyrecyclerview.demo.viewmodel
//
//import android.view.ViewGroup
//import androidx.paging.PagingDataAdapter
//import androidx.recyclerview.widget.DiffUtil
//import cc.easyandroid.easyrecyclerview.demo.data.RedditPost
//import com.house101.app1.kt.adapter.EasyAda
//
///**
// * A simple adapter implementation that shows Reddit posts.
// */
//class PostsAdapter1()
//    : EasyAda<IFHolder_Kt, RedditPostViewHolder>(POST_COMPARATOR) {
//
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
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedditPostViewHolder {
//        return RedditPostViewHolder.create(parent)
//    }
//
//    companion object {
//        private val PAYLOAD_SCORE = Any()
//        val POST_COMPARATOR = object : DiffUtil.ItemCallback<IFHolder_Kt>() {
//            override fun areContentsTheSame(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Boolean =
//                    oldItem == newItem
//
//            override fun areItemsTheSame(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Boolean =
//                    oldItem.name == newItem.name
//
//            override fun getChangePayload(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Any? {
//                return if (sameExceptScore(oldItem, newItem)) {
//                    PAYLOAD_SCORE
//                } else {
//                    null
//                }
//            }
//        }
//
//        private fun sameExceptScore(oldItem: IFHolder_Kt, newItem: IFHolder_Kt): Boolean {
//            // DON'T do this copy in a real app, it is just convenient here for the demo :)
//            // because reddit randomizes scores, we want to pass it as a payload to minimize
//            // UI updates between refreshes
//            return oldItem.copy(score = newItem.score) == newItem
//        }
//    }
//}
