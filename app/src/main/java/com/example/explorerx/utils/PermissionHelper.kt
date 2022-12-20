package com.example.explorerx.utils

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.explorerx.ExApplication

fun isGranted(vararg permissions: String): Boolean {
    for (permission in permissions)
        if (ContextCompat.checkSelfPermission(
                ExApplication.context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        )
            return false
    return true
}
