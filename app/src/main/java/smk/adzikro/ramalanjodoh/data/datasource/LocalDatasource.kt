package smk.adzikro.ramalanjodoh.data.datasource

import smk.adzikro.ramalanjodoh.data.local.DbRamal
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Userx
import javax.inject.Inject

class LocalDatasource @Inject constructor(
    private val db: DbRamal
) {

    /*Ramalan*/
    fun addRamal(ramal: Ramal) = db.ramal().addRamal(ramal)
    suspend fun getListAll() = db.ramal().getListAll()
    suspend fun getRamal(id: Int) = db.ramal().getRamal(id)
    suspend fun updateRamal(ramal: Ramal) = db.ramal().updateRamal(ramal)
    suspend fun deleteRamal(ramal: Ramal) = db.ramal().deleteRamal(ramal)

    /*User*/
    fun addUserx(user: Userx) = db.user().addUserx(user)
    suspend fun getUserx(id: String) = db.user().getUserx(id)
    suspend fun updateName(uid: String, displayName: String) = db.user().updateName(uid, displayName)

}