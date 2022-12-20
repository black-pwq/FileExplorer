package com.example.explorerx.ui.treechart

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.explorerx.ui.common.FileEntryDbVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "TreeMapViewModel"

class TreeMapViewModel(fileUriStr: String) : FileEntryDbVM(fileUriStr) {
    private val _currDir: MutableStateFlow<String> = MutableStateFlow(mountPath)
    val currDir = _currDir.asStateFlow()

    init {
        _currDir.value = mountPath
    }

    inner class OnEnterDirCallBack {

        @JavascriptInterface
        fun setCurrDir(parentPath: String) {
            Log.d(TAG, "setting curr dir")
            _currDir.value = parentPath
            Log.d(TAG, "curr dir is ${currDir.value}")
        }
    }
}

class TreeMapViewModelFactory(
    private val fileUriStr: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TreeMapViewModel(fileUriStr) as T
    }
}