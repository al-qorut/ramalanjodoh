package smk.adzikro.ramalanjodoh.data.datasource

import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Userx
import smk.adzikro.ramalanjodoh.data.remote.FireStore
import javax.inject.Inject

class RemoteDatasource @Inject constructor(
    private val remote: FireStore
){
    suspend fun addUser(user: Userx) : String {
        return remote.addUser(user)
    }
    suspend fun getUser(id: String) : Userx? {
        return remote.getuser(id)
    }

    suspend fun addRamalx(ramal: Ramal) : String {
        return remote.addRamal(ramal)
    }

    fun getQueryRamal() = remote.getQueryRamal()

    fun getQueryCariRamal(nama : String) = remote.getQueryCariRamal(nama)

    suspend fun toggleFavorite(ramalid: String) = remote.toggleFavorite(ramalid)

    fun getQueryMessage(ramalid: String) = remote.getQueryMessage(ramalid)

    suspend fun addComment(ramalid: String, message: String) = remote.addComment(ramalid, message)

    suspend fun getToken() = remote.getToken()
    suspend fun addTokenBonus() = remote.addTokenBonus()
    suspend fun addBeliToken(count : Long) = remote.addBeliToken(count)
    suspend fun useToken()  = remote.useToken()
}