package smk.adzikro.ramalanjodoh.data.repo

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import smk.adzikro.ramalanjodoh.data.models.Ramal

interface RepoRamal {
    suspend fun getListAll(): List<Ramal>
    suspend fun getRamal(id: Int): Ramal?
    fun addRamal(ramal: Ramal) : Long
    fun getListRamal(): Flow<PagingData<Ramal>>
    suspend fun getPagedList(limit: Int, offset: Int): List<Ramal>
    suspend fun deleteRamal(ramal: Ramal) : Int
    suspend fun updateRamal(ramal: Ramal) : Int
    suspend fun getListKata():List<String>

    suspend fun getForbiddenWords() : HashSet<String>

}