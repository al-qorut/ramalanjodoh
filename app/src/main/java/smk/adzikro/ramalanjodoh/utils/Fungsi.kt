package smk.adzikro.ramalanjodoh.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import smk.adzikro.ramalanjodoh.R
import java.io.File
import java.io.FileOutputStream

fun confirmDialog(
    context: Context,
    message: String,
    onYesClicked: () -> Unit,
    onNoClicked: () -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setMessage(message)
        .setPositiveButton("Yes") { dialog, _ ->
            onYesClicked()
            dialog.dismiss()
        }
        .setNegativeButton("No") { dialog, _ ->
            onNoClicked()
            dialog.dismiss()
        }
        .setCancelable(false)

    builder.create().show()
}

fun captureViewAsBitmap(view: View): Bitmap {
    val width = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
    val height = View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY)
    view.measure(width, height)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}
fun saveBitmapToCache(bitmap: Bitmap, context: Context): Uri {
    // Mendapatkan direktori cache aplikasi
    val cachePath = File(context.cacheDir, "images")
    if (!cachePath.exists()) {
        cachePath.mkdirs()  // Membuat direktori jika belum ada
    }
    // Menyimpan gambar sebagai file PNG
    val file = File(cachePath, "shared_image.png")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.close()
    // Membuat URI file yang disimpan
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

fun shareImage(context: Context, bitmap: Bitmap) {
    val uri = saveBitmapToCache(bitmap, context)
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/png"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share image"))
}


fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

val Context.config: Config get() = Config.newInstance(applicationContext)

fun Activity.setupDialogStuff(
    view: View,
    dialog: AlertDialog.Builder,
    titleId: Int = 0,
    cancelOnTouchOutside: Boolean = true,
    callback: ((alertDialog: AlertDialog) -> Unit)? = null
) {
    if (isDestroyed || isFinishing) {
        return
    }

    var title: TextView? = null
    if (titleId != 0) {
        title = layoutInflater.inflate(R.layout.dialog_title, null) as TextView
        title.setText(titleId)
    }
    dialog.create().apply {
        setView(view)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCustomTitle(title)
        setCanceledOnTouchOutside(cancelOnTouchOutside)
        if (!isFinishing) {
            show()
        }
        val bgDrawable = resources.getDrawable(R.drawable.dialog_you_background, theme)
        window?.setBackgroundDrawable(bgDrawable)
        callback?.invoke(this)
    }
}