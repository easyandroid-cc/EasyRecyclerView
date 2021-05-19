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

package cc.easyandroid.easyrecyclerview.demo.source

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Append
import androidx.paging.PagingSource.LoadParams.Prepend
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import cc.easyandroid.easyrecyclerview.demo.data.RedditPost
import cc.easyandroid.easyrecyclerview.demo.viewmodel.IFHolder_Kt
import com.android.example.paging.pagingwithnetwork.reddit.api.RedditApi
import retrofit2.HttpException
import java.io.IOException

/**
 * A [PagingSource] that uses the before/after keys returned in page requests.
 *
 * @see ItemKeyedSubredditPagingSource
 */
class PageKeyedSubredditPagingSource(
    private val redditApi: RedditApi,
    private val subredditName: String
) : PagingSource<String, IFHolder_Kt>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, IFHolder_Kt> {
        return try {
            println("cgp =$subredditName")
            val data = redditApi.getTop(
                subreddit = subredditName,
                after = if (params is Append) params.key else null,
                before = if (params is Prepend) params.key else null,
                limit = params.loadSize
            ).data
            println("cgp subredditName=$subredditName")
            println("cgp data=${data.children}")
            Page(
                data = data.children.map { IFHolder_Kt(it.data )},
                prevKey = data.before,
                nextKey = data.after
            )
        } catch (e: IOException) {
            println("cgp1 e=$e")
            LoadResult.Error(e)
             //LoadResult.Error(Exception("test"))
        } catch (e: HttpException) {
            println("cgp2 e=$e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, IFHolder_Kt>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
