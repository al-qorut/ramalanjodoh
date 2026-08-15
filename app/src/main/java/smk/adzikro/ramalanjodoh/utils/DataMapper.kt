package smk.adzikro.ramalanjodoh.utils
import com.google.firebase.auth.FirebaseUser
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Ramalx
import smk.adzikro.ramalanjodoh.data.models.Userx
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


fun toUser(user :FirebaseUser) : Userx {
    return Userx(
        uid = user.uid,
        displayName = user.displayName.toString(),
        email = user.email.toString()
    )
}
fun toTimestamp(dateString: String): Date? {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return try {
        val date = format.parse(dateString)
        date
    } catch (e: Exception) {
        null // Jika terjadi kesalahan dalam parsing
    }
}

fun dateToString(date: Date?): String {
    val dateFormat = SimpleDateFormat("EEE, MMM yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(date)
}

fun toRamalx(ramal : Ramal) : Ramalx {
    return Ramalx(
        ramalid = UUID.randomUUID().toString(),
        pria = ramal.pria,
        wanita = ramal.wanita,
        desc = ramal.desc,
        date = toTimestamp(ramal.date),
        img = ramal.ilustratsi,
        favorite = listOf()
    )
}

fun toRamal(ramalx: Ramalx) : Ramal {
    return Ramal(
        pria = ramalx.pria,
        wanita = ramalx.wanita,
        desc = ramalx.desc,
        ilustratsi = ramalx.img
    )
}
