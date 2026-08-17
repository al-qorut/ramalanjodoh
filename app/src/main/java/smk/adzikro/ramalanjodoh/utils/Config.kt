package smk.adzikro.ramalanjodoh.utils

import android.content.Context
import javax.inject.Inject

open class Config @Inject constructor(val context: Context) {
    protected val prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE)
    companion object{
        fun newInstance(context: Context) = Config(context)
    }

    var userUid: String?
        get() = prefs.getString(UID,"")
        set(value) = prefs.edit().putString(UID, value).apply()

    var displayName: String?
        get() = prefs.getString(DISPLAY_NAME,"")
        set(value) = prefs.edit().putString(DISPLAY_NAME, value).apply()

    var email: String?
        get() = prefs.getString(EMAIL,"")
        set(value) = prefs.edit().putString(EMAIL, value).apply()

    var isResulPublish: Boolean
        get() = prefs.getBoolean(RESULT_PUBLISH,false)
        set(value) = prefs.edit().putBoolean(RESULT_PUBLISH, value).apply()

    var isAnalisisPro: Boolean
        get() = prefs.getBoolean(ANALISIS_PRO,false)
        set(value) = prefs.edit().putBoolean(ANALISIS_PRO, value).apply()

    var isHitungPro: Boolean
        get() = prefs.getBoolean(HITUNG_PRO,false)
        set(value) = prefs.edit().putBoolean(HITUNG_PRO, value).apply()

    var isInfoShow: Boolean
        get() = prefs.getBoolean(INFO_SHOW,true)
        set(value) = prefs.edit().putBoolean(INFO_SHOW, value).apply()


}