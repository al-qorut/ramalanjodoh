package smk.adzikro.ramalanjodoh.utils

import android.content.Context
import android.os.Build
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal


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

        // 2. Cek Panjang Karakter (Min 3, Max 60)
        if (trimmed.length < 3) {
            return "Nama $label minimal terdiri dari 3 huruf."
        }
        if (trimmed.length > 60) {
            return "Nama $label maksimal terdiri dari 60 karakter."
        }

        // 3. Hanya Izinkan Huruf dan Spasi (Blokir Angka & Karakter Spesial)
        // Regex ini menolak semua angka (0-9) dan simbol seperti @, #, $, dll.
        val alphabetAndSpaceRegex = "^[a-zA-Z\\s]+$".toRegex()
        if (!trimmed.matches(alphabetAndSpaceRegex)) {
            return "Nama $label hanya boleh berisi huruf dan spasi."
        }

        // 4. Cek Huruf Mati (Konsonan) Berurutan Terlalu Banyak (Contoh: "bcdfgh", "qwerty")
        // Nama manusia normal jarang memiliki lebih dari 3 atau 4 huruf konsonan berurutan tanpa vokal.
        val excessiveConsonantsRegex = "(?i)[bcdfghjklmnpqrstvwxyz]{4,}".toRegex()
        if (excessiveConsonantsRegex.containsMatchIn(trimmed)) {
            return "Nama $label tidak valid (kombinasi huruf tidak wajar)."
        }

        // 5. Cek Huruf Vokal Berurutan Terlalu Banyak (Contoh: "aaaaaa")
        val excessiveVowelsRegex = "(?i)[aeiou]{4,}".toRegex()
        if (excessiveVowelsRegex.containsMatchIn(trimmed)) {
            return "Nama $label tidak valid (huruf vokal berulang)."
        }

        // 6. Cek 3 Huruf Berurutan Sama (contoh: aaa, zzz)
        val tripleCharRegex = "([a-zA-Z])\\1\\1".toRegex()
        if (tripleCharRegex.containsMatchIn(trimmed)) {
            return "Nama $label tidak valid (terdapat karakter berulang)."
        }

        // 7. Cek Kata Terlarang (Gunakan lowercase agar pencarian akurat)
        if (forbiddenWords.contains(trimmed.lowercase())) {
            return "Nama '$name' tidak boleh digunakan. Silahkan ubah."
        }

        return null // Validasi lolos
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
        val ilustrasi: Int
        val desc: String
        val result : Int
        if (isActionPro) {
            val total = (men + women) % 7
            when (total) {
                0 -> {
                    result = IS_GOOD
                    ilustrasi = imgGood.indices.random()
                    desc = context.getString(R.string.ar0, kata1, kata2)
                }
                1 -> {
                    result = IS_GOOD
                    ilustrasi = imgGood.indices.random()
                    desc = context.getString(R.string.ar1, kata1, kata2)
                }
                2 -> {
                    result = IS_GOOD
                    ilustrasi = imgGood.indices.random()
                    desc = context.getString(R.string.ar2, kata1, kata2)
                }
                3 -> {
                    result = IS_GOOD
                    ilustrasi = imgGood.indices.random()
                    desc = context.getString(R.string.ar3, kata1, kata2)
                }
                4 -> {
                    result = IS_BAD
                    ilustrasi = imgGood.indices.random()
                    desc = context.getString(R.string.ar4, kata1, kata2)
                }
                5 -> {
                    result = IS_BAD
                    ilustrasi = imgBad.indices.random()
                    desc = context.getString(R.string.ar5, kata1, kata2)
                }
                else -> { // 6
                    result = IS_BAD
                    ilustrasi = imgBad.indices.random()
                    desc = context.getString(R.string.ar6, kata1, kata2)
                }
            }
        } else {
            val total = (men + women) % 3
            val kata3 = context.getString(R.string.disclimer)

            when (total) {
                0 -> {
                    result = IS_GOOD
                    ilustrasi = imgGood.indices.random()
                    desc = context.getString(kataBaik.random(), kata1, kata2)
                }
                1 -> {
                    result = IS_GOOD
                    ilustrasi = imgGood.indices.random()
                    desc = context.getString(kataBaik.random(), kata1, kata2)
                }
                else -> { // 2
                    result = IS_BAD
                    ilustrasi = imgBad.indices.random()
                    desc = context.getString(kataBad.random(), kata1, kata2, kata3)
                }
            }
        }

        val hasil = Ramal(
            id = 0,
            pria = kata1.trim(),
            wanita = kata2.trim(),
            desc = desc,
            ilustratsi = ilustrasi,
            result = result
        )
        onSuccess(hasil)
    }
}

