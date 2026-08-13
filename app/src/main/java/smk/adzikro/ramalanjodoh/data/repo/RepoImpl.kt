package smk.adzikro.ramalanjodoh.data.repo

import com.google.firebase.firestore.Query
import smk.adzikro.ramalanjodoh.data.datasource.LocalDatasource
import smk.adzikro.ramalanjodoh.data.datasource.RemoteDatasource
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Userx
import javax.inject.Inject

class RepoImpl @Inject constructor (
    private val remote: RemoteDatasource,
    private val local: LocalDatasource
) : Repositories {
    override fun addRamal(ramal: Ramal) {
        local.addRamal(ramal)
    }

    override suspend fun getListAll(): List<Ramal> {
        return local.getListAll()
    }

    override suspend fun getRamal(id: Int): Ramal? {
        return local.getRamal(id)
    }

    override suspend fun updateRamal(ramal: Ramal) {
        local.updateRamal(ramal)
    }

    override suspend fun deleteRamal(ramal: Ramal) {
        local.deleteRamal(ramal)
    }

    override fun addUserx(user: Userx) {
        local.addUserx(user)
    }

    override suspend fun getUserx(id: String): Userx? {
        return local.getUserx(id)
    }

    override suspend fun updateName(uid: String, displayName: String) {
        local.updateName(uid, displayName)
    }



    override suspend fun adduser(user: Userx): String {
        return remote.addUser(user)
    }

    override suspend fun getUser(id: String): Userx? {
        return remote.getUser(id)
    }

    override suspend fun addRamalx(ramal: Ramal): String {
        return remote.addRamalx(ramal)
    }

    override fun getQueryRamal(): Query {
        return remote.getQueryRamal()
    }

    override suspend fun toggleFavorite(ramalid: String): String {
        return remote.toggleFavorite(ramalid)
    }

    override fun getQueryMessage(ramalid: String): Query {
        return remote.getQueryMessage(ramalid)
    }

    override fun getQueryCariRamal(nama: String): Query {
        return remote.getQueryCariRamal(nama)
    }

    override suspend fun addComment(ramalid: String, message: String) : String {
       return remote.addComment(ramalid, message)
    }

    override suspend fun getToken(): Int {
        return remote.getToken()
    }
    //fun addBeliToken(count : Long, onSuccess: (Long) -> Unit, onFailure: (Exception) -> Unit)
    override fun addBeliToken(count: Long, onSuccess: (Long) -> Unit, onFailure: (Exception) -> Unit) {
        remote.addBeliToken(count, onSuccess, onFailure)
    }

    override suspend fun addTokenBonus() {
        remote.addTokenBonus()
    }

    override suspend fun useToken() {
        remote.useToken()
    }
}