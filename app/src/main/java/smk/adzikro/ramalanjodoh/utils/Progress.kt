package smk.adzikro.ramalanjodoh.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import smk.adzikro.ramalanjodoh.R

class Progress {
    private var view: View? = null
    private var builder: AlertDialog.Builder
    private var dialog: Dialog

    // Konstruktor Utama menggunakan String (agar cocok dengan fungsi onProgress Anda)
    constructor(context: Context, message: String, cancelable: Boolean = false) {
        view = LayoutInflater.from(context).inflate(R.layout.progrss, null)
        view?.findViewById<TextView>(R.id.text)?.text = message

        builder = AlertDialog.Builder(context)
        builder.setView(view)
        dialog = builder.create()
        dialog.setCancelable(cancelable)
    }

    // Konstruktor Tambahan menggunakan StringRes Int (jika sewaktu-waktu masih dibutuhkan)
    constructor(context: Context, @StringRes titleRes: Int, cancelable: Boolean = false) :
            this(context, context.getString(titleRes), cancelable)

    // Mengubah pesan menggunakan String Resource ID
    fun setProgressMessage(@StringRes titleRes: Int) {
        val context = view?.context
        if (context != null) {
            view?.findViewById<TextView>(R.id.text)?.text = context.getString(titleRes)
        }
    }

    // Mengubah pesan menggunakan String teks biasa
    fun setInfo(info: String) {
        view?.findViewById<TextView>(R.id.text)?.text = info
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
