package com.example.explorerx.ui.dialog

import android.content.Context
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.example.explorerx.R
import kotlin.system.exitProcess

private const val TAG = "dialog"

fun buildPositiveOnDialog(@NonNull context: Context, onPositiveBtnClick: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.alert_dialog_need_permission_title))
        .setMessage(context.getString(R.string.alert_dialog_need_permission_msg))
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.alert_dialog_need_permission_pbtn) ) {
                dialog, which -> onPositiveBtnClick()
        }.setNegativeButton(context.getString(R.string.alert_dialog_need_permission_nbtn)) {
                _, _  -> exitProcess(0)
        }
        .show()
}