package smk.adzikro.ramalanjodoh.data.repo

import com.google.firebase.firestore.Query
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Userx

interface Repositories {

    //localdatasource
    fun addRamal(ramal: Ramal)
    suspend fun getListAll() : List<Ramal>
    suspend fun getRamal(id: Int) : Ramal?
    suspend fun updateRamal(ramal: Ramal)
    suspend fun deleteRamal(ramal: Ramal)

    fun addUserx(user: Userx)
    suspend fun getUserx(id: String) : Userx?
    suspend fun updateName(uid: String, displayName: String)

    //remotedatasource
    suspend fun adduser(user: Userx) : String
    suspend fun getUser(id: String) : Userx?
    suspend fun addRamalx(ramal: Ramal) : String
    fun getQueryRamal() : Query
    fun getQueryCariRamal(nama : String) : Query
    suspend fun toggleFavorite(ramalid: String) : String

    //comment
    fun getQueryMessage(ramalid: String) : Query
    suspend fun addComment(ramalid: String, message: String) :String

    //token
    suspend fun getToken() : Int
    suspend fun addTokenBonus()
    fun addBeliToken(count : Long, onSuccess: (Long) -> Unit, onFailure: (Exception) -> Unit)
    suspend fun useToken()

}