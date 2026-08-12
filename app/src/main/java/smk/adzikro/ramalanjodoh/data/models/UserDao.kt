package smk.adzikro.ramalanjodoh.data.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUserx(user: Userx): Long

    @Query("SELECT * FROM user WHERE uid = :id")
    suspend fun getUserx(id: String): Userx?

    @Query("UPDATE user SET displayName = :displayName WHERE uid = :uid")
    suspend fun updateName(uid: String, displayName: String)

}