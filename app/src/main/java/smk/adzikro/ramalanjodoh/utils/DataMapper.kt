package smk.adzikro.ramalanjodoh.utils
import android.content.Context
import com.google.firebase.auth.FirebaseUser
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.Ramalx
import smk.adzikro.ramalanjodoh.data.models.Userx
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object DataMapper {

    val imgGood: Array<Int> = arrayOf(
        R.drawable.baik1,
        R.drawable.baik2,
        R.drawable.baik3,
        R.drawable.baik4,
        R.drawable.baik5,
        R.drawable.baik6,
        R.drawable.baik7,
        R.drawable.baik8,
        R.drawable.baik9,
        R.drawable.baik10,
        R.drawable.baik11,
        R.drawable.baik12,
        R.drawable.baik13,
        R.drawable.baik14,
        R.drawable.baik15,
        R.drawable.baik16,
        R.drawable.baik17,
        R.drawable.baik18,
        R.drawable.baik19,
        R.drawable.baik20,
        R.drawable.baik21,
        R.drawable.baik22,
        R.drawable.baik23,
        R.drawable.baik24,
        R.drawable.baik25,
        R.drawable.baik26,
        R.drawable.baik27,
        R.drawable.baik28,
        R.drawable.baik29,
        R.drawable.baik30
    )
    val imgSuccess: Array<Int> = arrayOf(
        R.drawable.sukses1,
        R.drawable.sukses2,
        R.drawable.sukses3,
        R.drawable.sukses4,
        R.drawable.sukses5,
        R.drawable.sukses6,
        R.drawable.sukses7,
        R.drawable.sukses8,
        R.drawable.sukses9,
        R.drawable.sukses10,
        R.drawable.sukses11,
        R.drawable.sukses12,
        R.drawable.sukses13,
        R.drawable.sukses14,
        R.drawable.sukses15,
        R.drawable.sukses16,
        R.drawable.sukses17,
        R.drawable.sukses18,
        R.drawable.sukses19,
        R.drawable.sukses20,
        R.drawable.sukses21,
        R.drawable.sukses22,
        R.drawable.sukses23,
        R.drawable.sukses24,
        R.drawable.sukses25,
        R.drawable.sukses26,
        R.drawable.sukses27,
        R.drawable.sukses28,
        R.drawable.sukses29,
        R.drawable.sukses29
    )
    val imgBad: Array<Int> = arrayOf(
        R.drawable.buruk1,
        R.drawable.buruk2,
        R.drawable.buruk3,
        R.drawable.buruk4,
        R.drawable.buruk5,
        R.drawable.buruk6,
        R.drawable.buruk7,
        R.drawable.buruk8,
        R.drawable.buruk9,
        R.drawable.buruk10,
        R.drawable.buruk11,
        R.drawable.buruk12,
        R.drawable.buruk13,
        R.drawable.buruk14,
        R.drawable.buruk15,
        R.drawable.buruk16,
        R.drawable.buruk17,
        R.drawable.buruk18,
        R.drawable.buruk19,
        R.drawable.buruk20,
        R.drawable.buruk21,
        R.drawable.buruk22,
        R.drawable.buruk23,
        R.drawable.buruk24,
        R.drawable.buruk25,
        R.drawable.buruk26,
        R.drawable.buruk27,
        R.drawable.buruk28,
        R.drawable.buruk29,
        R.drawable.buruk30,
        R.drawable.buruk31,
        R.drawable.buruk32,
        R.drawable.buruk33,
        R.drawable.buruk34,
        R.drawable.buruk35,
        R.drawable.buruk36,
        R.drawable.buruk37,
        R.drawable.buruk38,
        R.drawable.buruk39,
        R.drawable.buruk40,
        R.drawable.buruk41,
        R.drawable.buruk42,
        R.drawable.buruk43,
        R.drawable.buruk44,
        R.drawable.buruk45,
        R.drawable.buruk46,
        R.drawable.buruk47,
        R.drawable.buruk48,
        R.drawable.buruk49,
        R.drawable.buruk50
    )
    fun getScore(): HashMap<String, Int> {
        return hashMapOf<String, Int>().apply {
            put("A", 1)
            put("B", 2)
            put("C", 3)
            put("D", 4)
            put("E", 5)
            put("F", 6)
            put("G", 7)
            put("H", 8)
            put("I", 9)
            put("J", 10)
            put("K", 20)
            put("L", 30)
            put("M", 40)
            put("N", 50)
            put("O", 60)
            put("P", 70)
            put("Q", 80)
            put("R", 90)
            put("S", 100)
            put("T", 200)
            put("U", 300)
            put("V", 400)
            put("W", 500)
            put("X", 600)
            put("Y", 700)
            put("Z", 800)
        }
    }

    fun getScoreAr(): HashMap<String, Int> {
        return hashMapOf<String, Int>().apply {
            put("ا", 1)
            put("ب", 2)
            put("ج", 3)
            put("د", 4)
            put("ه", 5)
            put("و", 6)
            put("ز", 7)
            put("ح", 8)
            put("ط", 9)
            put("ي", 10)
            put("ك", 20)
            put("ل", 30)
            put("م", 40)
            put("ن", 50)
            put("س", 60)
            put("ع", 70)
            put("ف", 80)
            put("ص", 90)
            put("ق", 100)
            put("ر", 200)
            put("ش", 300)
            put("ت", 400)
            put("ث", 500)
            put("خ", 600)
            put("ذ", 700)
            put("ض", 800)
            put("ظ", 900)
            put("غ", 1000)
        }
    }
    val kataBaik = arrayOf(
        R.string.baik1,
        R.string.baik2,
        R.string.baik3,
        R.string.baik4,
        R.string.baik5,
        R.string.baik6,
        R.string.baik7,
        R.string.baik8,
        R.string.baik9,
        R.string.baik10,
        R.string.baik11
    )
    val kataBad = arrayOf(
        R.string.buruk1,
        R.string.buruk2,
        R.string.buruk3,
        R.string.buruk4,
        R.string.buruk5,
        R.string.buruk6,
        R.string.buruk7,
        R.string.buruk8,
        R.string.buruk9,
        R.string.buruk10
    )

    fun isArabic(input: String): Boolean {
        val arabRegex = "[\u0600-\u06FF]".toRegex()
        return arabRegex.containsMatchIn(input)
    }

    fun nameToScore(kata: String): Int {
        var skorMap: HashMap<String, Int>
        if (isArabic(kata)) {
            skorMap = getScoreAr()
        } else {
            skorMap = getScore()
        }

        var totalSkor = 0
        for (char in kata.uppercase()) {
            totalSkor += skorMap[char.toString()] ?: 0
        }
        //Log.e("DataMapper","hasil $totalSkor")
        return totalSkor
    }

    fun genResult(context: Context, kata1: String, kata2: String, result: (Ramal)->Unit) {
        val men = nameToScore(kata1);
        val women = nameToScore(kata2);
        var total = 0
        var ilustrasi = 0;
        var desc = "";

        if(isArabic(kata1) && isArabic(kata2)){
            total = (men + women) % 7
         //   Log.e("DataMapper","hasil $total")
            when (total) {
                0 -> {
                    ilustrasi = imgGood[(0..29).random()]
                    desc = context.getString(R.string.ar0, kata1, kata2)

                }
                1 -> {
                    ilustrasi = imgGood[(0..29).random()]
                    desc = context.getString(R.string.ar1, kata1, kata2)

                }
                2 -> {
                    ilustrasi = imgGood[(0..29).random()]
                    desc = context.getString(R.string.ar2, kata1, kata2)

                }
                3 -> {
                    ilustrasi = imgGood[(0..29).random()]
                    desc = context.getString(R.string.ar3, kata1, kata2)

                }
                4 -> {
                    ilustrasi = imgBad[(0..49).random()]
                    desc = context.getString(R.string.ar4, kata1, kata2)

                }
                5 -> {
                    ilustrasi = imgBad[(0..49).random()]
                    desc = context.getString(R.string.ar5, kata1, kata2)

                }
                6 -> {
                    ilustrasi = imgBad[(0..49).random()]
                    desc = context.getString(R.string.ar6, kata1, kata2)

                }
            }
        }else {
            total = (men + women) % 3

            val kata3 = context.getString(R.string.disclimer)
            when (total) {
                0 -> {
                    ilustrasi = imgGood[(0..29).random()]
                    desc = context.getString(kataBaik[(0..10).random()], kata1, kata2)
                }

                1 -> {
                    ilustrasi = imgSuccess[(0..28).random()]
                    desc = context.getString(kataBaik[(0..10).random()], kata1, kata2)
                }

                2 -> {
                    ilustrasi = imgBad[(0..49).random()]
                    desc = context.getString(kataBad[(0..9).random()], kata1, kata2, kata3)
                }

            }
        }
        var hasil = Ramal(id=0,
            pria = kata1, wanita = kata2,
            desc = desc, ilustratsi =  ilustrasi)
        result(hasil)
    }

}

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
