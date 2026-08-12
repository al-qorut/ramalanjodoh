package smk.adzikro.ramalanjodoh.data.repo

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.RamalDao
import java.io.BufferedReader
import java.io.InputStreamReader

import javax.inject.Inject

class Ramalimpl @Inject constructor(
    private val context: Context,
    private val dao: RamalDao) :RepoRamal {
    override suspend fun getListAll(): List<Ramal> {
        return dao.getListAll()
    }

    override suspend fun getRamal(id: Int): Ramal? {
        return dao.getRamal(id)
    }

    override fun addRamal(ramal: Ramal) : Long {
        return dao.addRamal(ramal)
    }

    override fun getListRamal(): Flow<PagingData<Ramal>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getRamalsPaging() }
        ).flow
    }

    override suspend fun getPagedList(limit: Int, offset: Int): List<Ramal> {
        return dao.getPagedList(limit, offset)
    }

    override suspend fun deleteRamal(ramal: Ramal) : Int {
      return  dao.deleteRamal(ramal)
    }

    override suspend fun updateRamal(ramal: Ramal): Int {
        return dao.updateRamal(ramal)
    }

    override suspend fun getListKata(): List<String> {
        val kataList = mutableListOf<String>()
        try {
            val inputStream = getApplication(context).resources.openRawResource(R.raw.katax)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.forEachLine { line ->
                kataList.add(line.trim())
            }
            withContext(Dispatchers.IO) {
                reader.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return kataList
    }

}