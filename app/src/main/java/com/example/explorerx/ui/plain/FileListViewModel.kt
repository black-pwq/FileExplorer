package com.example.explorerx.ui.plain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.explorerx.ui.common.FileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

private const val TAG = "FileListViewModel"

class FileListViewModel(fileUriStr: String) : FileViewModel(fileUriStr) {

    private val _files: MutableStateFlow<MutableList<File>?> = MutableStateFlow(null)
    val files = _files.asStateFlow()

    init {
        val array = mountPoint.listFiles()
        array?.let {
            _files.value = it.toMutableList()
        }
    }

    fun refresh() {
        if (_files.value == null)
            return
        val array = mountPoint.listFiles()
        array?.let {
            _files.value = it.toMutableList()
        }
    }

    fun search(regex: String) {
        val array = mountPoint.listFiles { it ->
            it.name.contains(regex)
        }
        array?.let { _files.value = it.toMutableList() }
    }
}

class FileListViewModelFactory(
    private val fileUriStr: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileListViewModel(fileUriStr) as T
    }
}