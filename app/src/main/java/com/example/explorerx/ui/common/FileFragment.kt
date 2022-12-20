package com.example.explorerx.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File

open class FileFragment : Fragment() {
    protected fun popFileChooser(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireActivity().applicationContext.packageName + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data = uri
        }
        startActivity(Intent.createChooser(intent, "Choose an app to open"))
    }
}