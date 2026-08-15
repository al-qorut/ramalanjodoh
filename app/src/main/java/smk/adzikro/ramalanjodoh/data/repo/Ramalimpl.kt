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

    @Volatile
    private var cachedForbiddenWords: HashSet<String>? = null

    override suspend fun getForbiddenWords(): HashSet<String> = withContext(Dispatchers.IO) {
        // Return dari cache jika sudah pernah dimuat
        cachedForbiddenWords?.let { return@withContext it }

        // Inisialisasi HashSet dengan perkiraan kapasitas (31k kata + buffer)
        // Menentukan initialCapacity mencegah pembesaran memori berulang kali
        val set = HashSet<String>(35000)

        try {
            context.resources.openRawResource(R.raw.katax).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                    lines.forEach { line ->
                        // Jika CSV memiliki baris kosong atau spasi, bersihkan dahulu
                        val trimmedWord = line.trim().lowercase()
                        if (trimmedWord.isNotEmpty()) {
                            // Jika CSV Anda dipisahkan koma dalam 1 baris, gunakan: line.split(",")
                            set.add(trimmedWord)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedForbiddenWords = set
        return@withContext set
    }

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