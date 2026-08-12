package smk.adzikro.ramalanjodoh.ui.adapter

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import smk.adzikro.ramalanjodoh.data.models.Comment
import smk.adzikro.ramalanjodoh.data.repo.Repositories
import javax.inject.Inject

class CommentPagingSource @Inject constructor(
    private val repo: Repositories,
    private var ramalid: String
) : PagingSource<QuerySnapshot, Comment>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Comment> {
        return try {
            val query = repo.getQueryMessage(ramalid)
            val currentPage = params.key ?: query.get().await()
            val lastVisibleComment = currentPage.documents[currentPage.size() - 1]
            val nextPage = query.startAfter(lastVisibleComment).get().await()
            Log.e("TAG", "load pagingAdapter : ${currentPage.toObjects(Comment::class.java).size}")
            LoadResult.Page(
                data = currentPage.toObjects(Comment::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Comment>): QuerySnapshot? {
        return null
    }
}