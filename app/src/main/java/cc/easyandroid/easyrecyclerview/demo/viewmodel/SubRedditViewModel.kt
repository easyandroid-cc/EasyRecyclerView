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

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cc.easyandroid.easyrecyclerview.demo.data.RedditPost
import cc.easyandroid.easyrecyclerview.demo.source.InMemoryByPageKeyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class SubRedditViewModel(private val repository: InMemoryByPageKeyRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val KEY_SUBREDDIT = "subreddit"
        const val DEFAULT_SUBREDDIT = "androiddev"
    }

    init {
        if (!savedStateHandle.contains(KEY_SUBREDDIT)) {
            savedStateHandle.set(KEY_SUBREDDIT, DEFAULT_SUBREDDIT)
        }
    }

    private val clearListCh = Channel<Unit>(Channel.CONFLATED)

//    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
  //  val posts = repository.postsOfSubreddit("Android", 10)
//
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val posts = flowOf(
             clearListCh.receiveAsFlow().map { PagingData.empty<IFHolder_Kt>() },
            savedStateHandle.getLiveData<String>(KEY_SUBREDDIT)
                    .asFlow()
                    .flatMapLatest { repository.postsOfSubreddit(it, 8) }
                    // cachedIn() shares the paging state across multiple consumers of posts,
                    // e.g. different generations of UI across rotation config change
                    .cachedIn(viewModelScope)
    ).flattenMerge(2)



    fun shouldShowSubreddit(
            subreddit: String
    ) = savedStateHandle.get<String>(KEY_SUBREDDIT) != subreddit

    fun showSubreddit(subreddit: String) {
        if (!shouldShowSubreddit(subreddit)) return

        clearListCh.offer(Unit)

        savedStateHandle.set(KEY_SUBREDDIT, subreddit)
    }
}
