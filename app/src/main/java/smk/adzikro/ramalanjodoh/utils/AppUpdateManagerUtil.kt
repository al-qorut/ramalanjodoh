package smk.adzikro.ramalanjodoh.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class AppUpdateManagerUtil(private val context: Context) {

    val appUpdateManager = AppUpdateManagerFactory.create(context)

    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    fun cekUpdate() {
        appUpdateInfoTask.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startUpdateFlow(updateInfo)
            }
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }
    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE, // Pilihan tipe pembaruan, bisa IMMEDIATE atau FLEXIBLE
                context as Activity,
                100) // Kode permintaan untuk menangani hasil pembaruan
        } catch (e: Exception) {
            // Tangani jika terjadi kesalahan
            e.printStackTrace()
        }
    }

    // Memastikan pembaruan selesai
    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == 100) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Pembaruan berhasil
                    Log.d("AppUpdate", "Update successful")
                    toast(context, "Update successful")
                }
                Activity.RESULT_CANCELED -> {
                    // Pengguna membatalkan pembaruan
                    Log.d("AppUpdate", "Update canceled")
                    toast(context, "Update canceled")
                }
                else -> {
                    // Terjadi kesalahan saat pembaruan
                    Log.d("AppUpdate", "Update failed")
                    toast(context, "Update failed")
                }
            }
        }
    }

}
