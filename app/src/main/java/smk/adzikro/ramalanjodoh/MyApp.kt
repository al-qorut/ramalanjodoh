package smk.adzikro.ramalanjodoh

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application(){

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (BuildConfig.DEBUG) {
            val debugSecret = "d59ccb04-00be-4f64-a73b-fd5532c8eb7c" // dari logcat
            val prefs = getSharedPreferences("firebase_app_check_debug", MODE_PRIVATE)
            prefs.edit().putString("com.google.firebase.appcheck.debug.DEBUG_SECRET", debugSecret).apply()
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }

}