package com.example.explorerx.ui.common

import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File


open class FileViewModel(fileUriStr: String) : ViewModel() {
    //    val files = mutableListOf<File>()
    val mountPath: String = Uri.parse(fileUriStr).path!!
    val mountPoint = File(mountPath)
}