package smk.adzikro.ramalanjodoh.data.remote

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import smk.adzikro.ramalanjodoh.data.models.Comment
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Userx
import smk.adzikro.ramalanjodoh.utils.PAGE_SIZE
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.toRamalx
import javax.inject.Inject

class FireStore @Inject constructor(
    private val context: Context,
    private val db : FirebaseFirestore) {

    suspend fun addUser(user: Userx): String {
        if (!user.isValid()) {
            return "Email tidak valid"
        }
        if (user.uid.isEmpty()) {
            return "UID tidak valid"
        }
        if (user==null) {
            return "User tidak valid"
        }
        val doc = db.collection("users").document(user.uid)
        return try {
            val snapshot = doc.get().await()
            if(!snapshot.exists()) {
              doc.set(user).await()
              return user.uid
            }else{
                return "User sudah ada"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Gagal menambahkan data"
        }
    }

    suspend fun addRamal(ramal: Ramal): String {
        val ramalx = toRamalx(ramal)
        if(context.config.userUid != null) {
            ramalx.uid = context.config.userUid!!
            ramalx.displayName = context.config.displayName
            val doc = db.collection("ramalan").document(ramalx.ramalid)
            return try {
                doc.set(ramalx).await()
                doc.id
            } catch (e: Exception) {
                e.printStackTrace()
                "terjadi error ${e.message}"
            }
        }else{
            return "uid tidak valid"
        }
    }

    suspend fun getuser(id: String) : Userx? {
        return try {
            val document = db.collection("users").document(id).get().await()
            if (document.exists()) {
                val user = document.toObject(Userx::class.java)
                user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    fun getQueryRamal() = db.collection("ramalan")
        .orderBy("date", Query.Direction.DESCENDING)
        .limit(PAGE_SIZE.toLong())

    fun getQueryCariRamal(nama : String) = db.collection("ramalan")
        .where(Filter.or(
            Filter.equalTo("displayName",nama),
            Filter.equalTo("pria",nama),
            Filter.equalTo("wanita",nama)
        ))
        .orderBy("date", Query.Direction.DESCENDING)
        .limit(PAGE_SIZE.toLong())

    fun getQueryMessage(ramalid: String) = db.collection("comments")
        .whereEqualTo("ramalid", ramalid)
        .orderBy("date", Query.Direction.DESCENDING)
        .limit(PAGE_SIZE.toLong())


    suspend fun toggleFavorite(ramalid: String): String {
        if (context.config.userUid != null) {
            val doc = db.collection("ramalan").document(ramalid)
            return try {
                val documentSnapshot = doc.get().await()
                val currentFavorites = documentSnapshot.get("favorite") as? List<String> ?: emptyList()
                if (currentFavorites.contains(context.config.userUid)) {
                    doc.update("favorite", FieldValue.arrayRemove(context.config.userUid)).await()
                    "Favorite berhasil dihapus"
                } else {
                    doc.update("favorite", FieldValue.arrayUnion(context.config.userUid)).await()
                    "Favorite berhasil ditambahkan"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Terjadi error: ${e.message}"
            }
        } else {
            return "uid tidak valid"
        }
    }
    suspend fun addComment(ramalid: String, message: String): String {
        val comment = Comment(
            commentid = db.collection("comments").document().id,
            uid = context.config.userUid!!,
            ramalid = ramalid,
            displayName = context.config.displayName!!,
            message = message
        )
        val doc = db.collection("ramalan").document(ramalid)
        doc.update("message", FieldValue.increment(1)).await()
        return try {
            db.collection("comments").document(comment.commentid).set(comment).await()
            "Komentar berhasil ditambahkan"
        } catch (e: Exception) {
            "ada error ${e.message}"
        }
    }
    suspend fun addTokenBonus(){
        try {
            val doc = db.collection("users").document(context.config.userUid!!)
            doc.update("token", FieldValue.increment(1)).await()
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    suspend fun useToken(){
        try {
            val doc = db.collection("users").document(context.config.userUid!!)
            doc.update("token", FieldValue.increment(-1)).await()
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    fun addBeliToken(
        count: Long,
        onSuccess: (Long) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userUid = context.config.userUid
        if (userUid == null) {
            onFailure(Exception("User UID is null"))
            return
        }

        val docRef = db.collection("users").document(userUid)

        db.runTransaction { transaction ->
            // 1. Wajib baca data lama terlebih dahulu di dalam transaksi
            val snapshot = transaction.get(docRef)
            val currentToken = snapshot.getLong("token") ?: 0L
            val newToken = currentToken + count

            // 2. Tulis data baru ke Firestore
            transaction.update(docRef, "token", newToken)

            // 3. Kembalikan nilai token baru hasil perhitungan transaksi
            newToken
        }.addOnSuccessListener { newToken ->
            // Dipanggil ketika transaksi sukses secara keseluruhan
            onSuccess(newToken)
        }.addOnFailureListener { exception ->
            // Dipanggil ketika transaksi gagal
            onFailure(exception)
        }
    }
    suspend fun getToken() : Int {
        return try {
            val document = db.collection("users").document(context.config.userUid!!).get().await()
            if (document.exists()) {
                val user = document.toObject(Userx::class.java)
                user!!.token
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}