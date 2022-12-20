package com.example.explorerx.ui.common

import androidx.lifecycle.viewModelScope
import com.example.explorerx.model.googlechart.FileEntry
import com.example.explorerx.repository.FileEntryRepo
import com.example.explorerx.repository.IORepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class FileEntryDbVM(fileUriStr: String): FileViewModel(fileUriStr) {
    private val fileEntryRepo = FileEntryRepo.get()
    private val _data: MutableStateFlow<List<FileEntry>?> = MutableStateFlow(null)
    private val _cached: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val data = _data.asStateFlow()
    val cached = _cached.asStateFlow()


    fun scanAndStore() {
        viewModelScope.launch {
            val data = IORepo.getTreeData(mountPoint)
            _data.value = (data + FileEntry(mountPath, null, 0))
            fileEntryRepo.deleteByPrefix(mountPath)
            fileEntryRepo.insertFileEntries(data)
            fileEntryRepo.insertFileEntry(FileEntry(mountPath, mountPoint.parent, 0))
        }
    }

    fun readCacheInDb() {
        viewModelScope.launch {
            val dataList = fileEntryRepo.findByPrefix("$mountPath/")
            if (dataList.isNotEmpty()) {
                val root = fileEntryRepo.loadById(mountPath)!!
                root.parentId = null
                _data.value = (dataList + root)
            } else {
                _data.value = emptyList()
            }
        }
    }

    fun testIfCached() {
        viewModelScope.launch {
            val entity = fileEntryRepo.loadById(mountPath)
            _cached.value = entity != null
        }
    }
}