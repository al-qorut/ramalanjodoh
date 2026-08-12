package smk.adzikro.ramalanjodoh.data.models

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RamalDao {

    @Query("SELECT * FROM ramal")
    suspend fun getListAll(): List<Ramal>

    @Query("SELECT * FROM ramal WHERE id = :id")
    suspend fun getRamal(id: Int): Ramal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRamal(ramal: Ramal) : Long

    @Query("SELECT * FROM ramal ORDER BY date DESC")
    fun getRamalsPaging(): PagingSource<Int, Ramal>

    @Query("SELECT * FROM ramal ORDER BY id DESC LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(limit: Int, offset: Int): List<Ramal>

    @Delete
    suspend fun deleteRamal(ramal: Ramal) : Int

    @Update
    suspend fun updateRamal(ramal: Ramal) : Int

}