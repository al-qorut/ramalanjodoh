package smk.adzikro.ramalanjodoh.data.remote

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import smk.adzikro.ramalanjodoh.data.models.Comment
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Userx
import smk.adzikro.ramalanjodoh.utils.IS_GOOD
import smk.adzikro.ramalanjodoh.utils.JodohHelper
import smk.adzikro.ramalanjodoh.utils.PAGE_SIZE
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.toRamalx
import smk.adzikro.ramalanjodoh.utils.toast
import javax.inject.Inject
import kotlin.coroutines.resume

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
            val ongkos : Long = if(context.config.isAnalisisPro && context.config.isHitungPro) -10L else -1L
            context.config.isHitungPro = false
            doc.update("token", FieldValue.increment(ongkos)).await()
        }catch (e :Exception){
            context.config.isHitungPro = false
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
    private suspend fun kawinkeunSynchronous(laki: String, wanita: String): Pair<Int, Int> {
        return suspendCancellableCoroutine { continuation ->
            val jodohHelper = JodohHelper()
            jodohHelper.genResult(
                context = context,
                kata1 = laki,
                kata2 = wanita,
                onSuccess = {
                    continuation.resume(Pair(it.ilustratsi, it.result))
                },
                onError = {
                    continuation.resume(Pair(0, IS_GOOD))
                }
            )
        }
    }

    // 2. Fungsi Migrasi Utama (Tambahkan modifier suspend)
    suspend fun migrasiIdGambarKeIndeksArray() {
        val db = FirebaseFirestore.getInstance()
        val koleksiRamal = db.collection("ramalan")

        try {
            // Menggunakan .await() dari kotlinx-coroutines-play-services
            val querySnapshot = koleksiRamal.get().await()

            if (querySnapshot.isEmpty) {
                Log.d("MIGRASI", "Tidak ada data untuk dimigrasi.")
                return
            }

            var batch = db.batch()
            var count = 0
            val totalData = querySnapshot.size()

            for (document in querySnapshot.documents) {
                val currentImgValue = document.getLong("img")?.toInt() ?: 0

                // ⚠️ KRUSIAL: Hanya migrasi jika data lama bernilai besar (bukan indeks 0..29)
                if (currentImgValue !in 0..60) {
                    val pria = document.getString("pria") ?: ""
                    val wanita = document.getString("wanita") ?: ""

                    // Menunggu hasil perhitungan dari JodohHelper selesai
                    val (imgIndex, hasil) = kawinkeunSynchronous(pria, wanita)

                    batch.update(document.reference, "img", imgIndex)
                    batch.update(document.reference, "result", hasil)
                    count++

                    // Batasan Firestore: 1 batch maksimal berisi 500 operasi write
                    if (count % 500 == 0) {
                        batch.commit().await()
                        batch = db.batch() // Buat batch baru untuk data selanjutnya
                    }
                }
            }

            // Komit sisa data di batch terakhir
            if (count % 500 != 0) {
                batch.commit().await()
                toast(context, "Sukses mengubah $count dari $totalData data lama ke sistem indeks.")
            } else if (count > 0) {
                toast(context, "Sukses mengubah $count data lama ke sistem indeks.")
            } else {
                toast(context, "Semua data sudah menggunakan format indeks array. Tidak ada perubahan.")
            }

        } catch (exception: Exception) {
            toast(context, "Gagal melakukan migrasi: ${exception.message}")
        }
    }
}