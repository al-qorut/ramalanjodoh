package smk.adzikro.ramalanjodoh.utils

import android.content.Context
import android.os.Build
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// ==========================================
// 1. DATA MODELS
// ==========================================

// Result Sealed Class untuk Menangani Validasi
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

// ==========================================
// 2. HELPER CLASS (JODOH HELPER)
// ==========================================

class JodohHelper {
    // List kata yang tidak boleh/tidak mungkin jadi nama manusia
    var forbiddenWords: Set<String> = emptySet()
    var isActionPro : Boolean = false
    var tokenCount : Int = 0
    companion object {
        // Regex untuk mendeteksi 3+ huruf sama berurutan (contoh: aaa, bbb)
        private val TRIPLE_CHAR_REGEX = Regex("(?i)(.)\\1{2,}")


        // Map Skor Latin
        private val SCORE_MAP_LATIN = mapOf(
            "A" to 1, "B" to 2, "C" to 3, "D" to 4, "E" to 5, "F" to 6,
            "G" to 7, "H" to 8, "I" to 9, "J" to 10, "K" to 20, "L" to 30,
            "M" to 40, "N" to 50, "O" to 60, "P" to 70, "Q" to 80, "R" to 90,
            "S" to 100, "T" to 200, "U" to 300, "V" to 400, "W" to 500,
            "X" to 600, "Y" to 700, "Z" to 800
        )

        // Map Skor Arab
        private val SCORE_MAP_ARABIC = mapOf(
            "ا" to 1, "ب" to 2, "ج" to 3, "د" to 4, "ه" to 5, "و" to 6,
            "ز" to 7, "ح" to 8, "ط" to 9, "ي" to 10, "ك" to 20, "ل" to 30,
            "م" to 40, "ن" to 50, "س" to 60, "ع" to 70, "ف" to 80, "ص" to 90,
            "ق" to 100, "ر" to 200, "ش" to 300, "ت" to 400, "ث" to 500,
            "خ" to 600, "ذ" to 700, "ض" to 800, "ظ" to 900, "غ" to 1000
        )

        val imgGood = arrayOf(
            R.drawable.baik1, R.drawable.baik2, R.drawable.baik3, R.drawable.baik4,
            R.drawable.baik5, R.drawable.baik6, R.drawable.baik7, R.drawable.baik8,
            R.drawable.baik9, R.drawable.baik10, R.drawable.baik11, R.drawable.baik12,
            R.drawable.baik13, R.drawable.baik14, R.drawable.baik15, R.drawable.baik16,
            R.drawable.baik17, R.drawable.baik18, R.drawable.baik19, R.drawable.baik20,
            R.drawable.baik21, R.drawable.baik22, R.drawable.baik23, R.drawable.baik24,
            R.drawable.baik25, R.drawable.baik26, R.drawable.baik27, R.drawable.baik28,
            R.drawable.baik29, R.drawable.baik30
        )

        val imgSuccess = arrayOf(
            R.drawable.sukses1, R.drawable.sukses2, R.drawable.sukses3, R.drawable.sukses4,
            R.drawable.sukses5, R.drawable.sukses6, R.drawable.sukses7, R.drawable.sukses8,
            R.drawable.sukses9, R.drawable.sukses10, R.drawable.sukses11, R.drawable.sukses12,
            R.drawable.sukses13, R.drawable.sukses14, R.drawable.sukses15, R.drawable.sukses16,
            R.drawable.sukses17, R.drawable.sukses18, R.drawable.sukses19, R.drawable.sukses20,
            R.drawable.sukses21, R.drawable.sukses22, R.drawable.sukses23, R.drawable.sukses24,
            R.drawable.sukses25, R.drawable.sukses26, R.drawable.sukses27, R.drawable.sukses28,
            R.drawable.sukses29, R.drawable.sukses29
        )

        val imgBad = arrayOf(
            R.drawable.buruk1, R.drawable.buruk2, R.drawable.buruk3, R.drawable.buruk4,
            R.drawable.buruk5, R.drawable.buruk6, R.drawable.buruk7, R.drawable.buruk8,
            R.drawable.buruk9, R.drawable.buruk10, R.drawable.buruk11, R.drawable.buruk12,
            R.drawable.buruk13, R.drawable.buruk14, R.drawable.buruk15, R.drawable.buruk16,
            R.drawable.buruk17, R.drawable.buruk18, R.drawable.buruk19, R.drawable.buruk20,
            R.drawable.buruk21, R.drawable.buruk22, R.drawable.buruk23, R.drawable.buruk24,
            R.drawable.buruk25, R.drawable.buruk26, R.drawable.buruk27, R.drawable.buruk28,
            R.drawable.buruk29, R.drawable.buruk30, R.drawable.buruk31, R.drawable.buruk32,
            R.drawable.buruk33, R.drawable.buruk34, R.drawable.buruk35, R.drawable.buruk36,
            R.drawable.buruk37, R.drawable.buruk38, R.drawable.buruk39, R.drawable.buruk40,
            R.drawable.buruk41, R.drawable.buruk42, R.drawable.buruk43, R.drawable.buruk44,
            R.drawable.buruk45, R.drawable.buruk46, R.drawable.buruk47, R.drawable.buruk48,
            R.drawable.buruk49, R.drawable.buruk50
        )

        val kataBaik = arrayOf(
            R.string.baik1, R.string.baik2, R.string.baik3, R.string.baik4,
            R.string.baik5, R.string.baik6, R.string.baik7, R.string.baik8,
            R.string.baik9, R.string.baik10, R.string.baik11
        )

        val kataBad = arrayOf(
            R.string.buruk1, R.string.buruk2, R.string.buruk3, R.string.buruk4,
            R.string.buruk5, R.string.buruk6, R.string.buruk7, R.string.buruk8,
            R.string.buruk9, R.string.buruk10
        )
    }

    // ==========================================
    // FUNGSI VALIDASI
    // ==========================================

    private fun validateSingleName(name: String, label: String): String? {
        val trimmed = name.trim()

        // 1. Cek Empty/Blank
        if (trimmed.isEmpty()) {
            return "Nama $label tidak boleh kosong."
        }

        // 2. Cek Panjang Minimal (Min 3 karakter)
        if (trimmed.length < 3) {
            return "Nama $label minimal terdiri dari 3 huruf."
        }

        // 3. Cek 3 Huruf Berurutan Sama (contoh: aaa, zzz)
        if (TRIPLE_CHAR_REGEX.containsMatchIn(trimmed)) {
            return "Nama $label tidak valid (terdapat karakter berulang acak)."
        }

        // 4. Cek Kata Terlarang
        if (forbiddenWords.contains(trimmed)) {
            return "Nama '$name' tidak boleh digunakan. silahkan ubah"
        }

        return null // Null menandakan validasi lolos
    }

    private fun validateInput(pria: String, wanita: String): ValidationResult {
        val priaError = validateSingleName(pria, "Pria")
        if (priaError != null) return ValidationResult.Error(priaError)

        val wanitaError = validateSingleName(wanita, "Wanita")
        if (wanitaError != null) return ValidationResult.Error(wanitaError)

        // Cek Apakah Pria == Wanita
        if (pria.trim().equals(wanita.trim(), ignoreCase = true)) {
            return ValidationResult.Error("Nama Pria dan Wanita tidak boleh sama.")
        }

        if(tokenCount<10 && isActionPro){
            return ValidationResult.Error("Analisis profesional memerlukan minimal 10 token")
        }
        return ValidationResult.Success
    }

    // ==========================================
    // LOGIKA PERHITUNGAN RAMALAN
    // ==========================================

    private fun isArabic(input: String): Boolean {
        val arabRegex = "[\u0600-\u06FF]".toRegex()
        return arabRegex.containsMatchIn(input)
    }

    private fun nameToScore(kata: String): Int {
        val skorMap = if (isArabic(kata)) SCORE_MAP_ARABIC else SCORE_MAP_LATIN
        var totalSkor = 0
        for (char in kata.uppercase()) {
            totalSkor += skorMap[char.toString()] ?: 0
        }
        return totalSkor
    }

    private fun convertLatinToArabic(inputText: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) { // Menargetkan API 29+ untuk Transliterator bawaan
            if (Build.VERSION.SDK_INT >= 29) {
                // Digunakan untuk Android 10 (API 29) ke atas
                val transliterator = android.icu.text.Transliterator.getInstance("Latin-Arabic")
                transliterator.transliterate(inputText)
            } else {
                // Digunakan untuk Android 8.0 (API 26) sampai Android 9 (API 28)
                val transliterator = com.ibm.icu.text.Transliterator.getInstance("Latin-Arabic")
                transliterator.transliterate(inputText)
            }
        } else {
            // Fallback tambahan jika suatu saat minSdk diturunkan lagi
            val transliterator = com.ibm.icu.text.Transliterator.getInstance("Latin-Arabic")
            transliterator.transliterate(inputText)
        }
    }

    fun genResult(
        context: Context,
        kata1: String,
        kata2: String,
        onSuccess: (Ramal) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        // 1. Jalankan Validasi Terlebih Dahulu
        when (val validation = validateInput(kata1, kata2)) {
            is ValidationResult.Error -> {
                onError?.invoke(validation.message)
                return
            }
            is ValidationResult.Success -> { /* Lanjut ke proses ramal */ }
        }

        // 2. Kalkulasi Skor
        val men = if(isActionPro) nameToScore(convertLatinToArabic(kata1)) else nameToScore(kata1)
        val women = if(isActionPro) nameToScore(convertLatinToArabic(kata2)) else nameToScore(kata2)
       // context.mydebug("${convertLatinToArabic(kata1)}, ${convertLatinToArabic(kata2)} laki $men we $women ")
        val ilustrasi: Int
        val desc: String

        if (isActionPro) {
            val total = (men + women) % 7
            when (total) {
                0 -> {
                    ilustrasi = imgGood.random()
                    desc = context.getString(R.string.ar0, kata1, kata2)
                }
                1 -> {
                    ilustrasi = imgGood.random()
                    desc = context.getString(R.string.ar1, kata1, kata2)
                }
                2 -> {
                    ilustrasi = imgGood.random()
                    desc = context.getString(R.string.ar2, kata1, kata2)
                }
                3 -> {
                    ilustrasi = imgGood.random()
                    desc = context.getString(R.string.ar3, kata1, kata2)
                }
                4 -> {
                    ilustrasi = imgBad.random()
                    desc = context.getString(R.string.ar4, kata1, kata2)
                }
                5 -> {
                    ilustrasi = imgBad.random()
                    desc = context.getString(R.string.ar5, kata1, kata2)
                }
                else -> { // 6
                    ilustrasi = imgBad.random()
                    desc = context.getString(R.string.ar6, kata1, kata2)
                }
            }
        } else {
            val total = (men + women) % 3
            val kata3 = context.getString(R.string.disclimer)

            when (total) {
                0 -> {
                    ilustrasi = imgGood.random()
                    desc = context.getString(kataBaik.random(), kata1, kata2)
                }
                1 -> {
                    ilustrasi = imgSuccess.random()
                    desc = context.getString(kataBaik.random(), kata1, kata2)
                }
                else -> { // 2
                    ilustrasi = imgBad.random()
                    desc = context.getString(kataBad.random(), kata1, kata2, kata3)
                }
            }
        }

        val hasil = Ramal(
            id = 0,
            pria = kata1.trim(),
            wanita = kata2.trim(),
            desc = desc,
            ilustratsi = ilustrasi
        )
        onSuccess(hasil)
    }
}

