package smk.adzikro.ramalanjodoh.ui.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import smk.adzikro.ramalanjodoh.data.models.Ramalx
import smk.adzikro.ramalanjodoh.data.repo.Repositories
import javax.inject.Inject

class RamalPagingSource @Inject constructor(
    private val repo: Repositories
) : PagingSource<QuerySnapshot, Ramalx>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Ramalx> {
        return try {
            val query = repo.getQueryRamal()
            val currentPage = params.key ?: query.get().await()
            val lastVisibleRamal = currentPage.documents[currentPage.size() - 1]
            val nextPage = query.startAfter(lastVisibleRamal).get().await()

            LoadResult.Page(
                data = currentPage.toObjects(Ramalx::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Ramalx>): QuerySnapshot? {
       return null
    }
}
