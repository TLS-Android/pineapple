package com.pineapple.app.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pineapple.app.model.reddit.PostItem
import com.pineapple.app.network.RedditNetworkProvider
import timber.log.Timber

data class RedditPageStore(val before: String?, val after: String?)

class NetworkPagingSource(
    private val service: RedditNetworkProvider,
    private val subreddit: String,
    private val sort: String,
    private val time: String
) : PagingSource<String, PostItem>() {

    private val keys: MutableMap<Int, RedditPageStore> = mutableMapOf()

    override fun getRefreshKey(state: PagingState<String, PostItem>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            keys[anchorPosition]?.before ?: keys[anchorPosition]?.after
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostItem> {
        val response = service.fetchSubreddit(name = subreddit, sort = sort, time = time, after = params.key)
        val nextKey = response.data.after
        val prevKey = response.data.before
        keys[keys.size] = RedditPageStore(prevKey, nextKey)
        Timber.tag("0").e("keys found: " + prevKey + " and " + nextKey)
        return if (response.data.children.isNotEmpty()) {
            Timber.tag("0").e("children aren't empty")
            LoadResult.Page(
                data = response.data.children,
                nextKey = nextKey,
                prevKey = prevKey
            )
        } else {
            Timber.e("0", "Children are empty")
            LoadResult.Page(listOf(), null, null)
        }
    }

}