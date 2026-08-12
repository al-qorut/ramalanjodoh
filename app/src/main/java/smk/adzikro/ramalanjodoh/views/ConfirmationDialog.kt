package com.alqorut.mystory.views

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.utils.setupDialogStuff

class ConfirmationDialog(
    activity: Activity, message:String = "", messageId: Int = R.string.pertanyaan, positive: Int = R.string.yes,
    negative: Int = R.string.no, val cancelOnTouchOutside: Boolean = true, val callback: (result: Boolean) -> Unit
) {
    private var dialog: AlertDialog? = null

    init {
        val view = activity.layoutInflater.inflate(R.layout.dialog_message, null)
        val msg = view.findViewById<TextView>(R.id.message)
        msg.text = if (message.isEmpty()) activity.resources.getString(messageId) else message

        val builder = AlertDialog.Builder(activity)
            .setPositiveButton(positive) { dialog, which -> positivePressed() }

        if (negative != 0) {
            builder.setNegativeButton(negative) { dialog, which -> negativePressed() }
        }

        if (!cancelOnTouchOutside) {
            builder.setOnCancelListener { negativePressed() }
        }

        builder.apply {
            activity.setupDialogStuff(view, this, cancelOnTouchOutside = cancelOnTouchOutside) { alertDialog ->
                dialog = alertDialog
            }
        }
    }

    private fun positivePressed() {
        dialog?.dismiss()
        callback(true)
    }

    private fun negativePressed() {
        dialog?.dismiss()
        callback(false)
    }
}
