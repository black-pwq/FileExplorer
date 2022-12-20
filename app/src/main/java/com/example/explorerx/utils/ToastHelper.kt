package com.example.explorerx.utils

import android.view.View
import android.widget.Toast
import com.example.explorerx.ExApplication
import com.google.android.material.snackbar.Snackbar

fun String.toast() {
    Toast.makeText(ExApplication.context, this, Toast.LENGTH_SHORT).show()
}

fun Int.toast() {
    Toast.makeText(ExApplication.context, this, Toast.LENGTH_SHORT).show()
}

fun View.snack(s: String) {
    Snackbar.make(this, s, Snackbar.LENGTH_SHORT).show()
}