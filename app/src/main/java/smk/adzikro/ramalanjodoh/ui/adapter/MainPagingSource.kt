package smk.adzikro.ramalanjodoh.ui.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.repo.RepoRamal
import javax.inject.Inject

class MainPagingSource @Inject constructor(
    private val repo: RepoRamal
) : PagingSource<Int,Ramal>(){

    override fun getRefreshKey(state: PagingState<Int, Ramal>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Ramal> {
        return try {
            val page = params.key ?: 0
            val entities = repo.getPagedList(params.loadSize, page *params.loadSize)
            if (page != 0) delay(1000)
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}