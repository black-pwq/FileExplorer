package com.example.explorerx.ui.sniffer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.explorerx.repository.FileEntryRepo
import com.example.explorerx.repository.IORepo
import com.example.explorerx.ui.common.FileEntryDbVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "SnifferViewModel"

class SnifferViewModel(fileUriStr: String) : FileEntryDbVM(fileUriStr) {
    private val _listOfList: MutableStateFlow<MutableList<File>?> = MutableStateFlow(null)
    val listOfList = _listOfList.asStateFlow()
    private val repo = FileEntryRepo.get()
    private var flag = true

    fun sniff() {
        viewModelScope.launch {
            repo.findFilesWithSameSizeByPrefix(mountPath).collect { size2Path ->
                for (size in size2Path.keys) {
                    size2Path[size]?.let { pathList ->
                        val ext2Path = extClassify(pathList)
                        for (ext in ext2Path.keys) {
                            val hash2File = hashClassify(ext2Path[ext]!!)
                            for (key in hash2File.keys) {
                                val list = hash2File[key]!!
                                if (list.size == 1)
                                    continue
                                _listOfList.value = list
                                flag = false
                            }
                        }
                    }
                }
                if (flag)
                    _listOfList.value = mutableListOf()
            }
        }
    }

    fun deleteFile(fullPath: String) {
        viewModelScope.launch {
            repo.deleteById(fullPath)
        }
    }

    private fun extClassify(pathList: List<String>): MutableMap<String, MutableList<String>> {
        val ext2Files = mutableMapOf<String, MutableList<String>>()
        for (path in pathList) {
            val index = path.lastIndexOf(File.separatorChar)
            val name = path.substring(index + 1)
            val ext = name.substringAfterLast('.', "")
            if (ext2Files[ext] == null) {
                ext2Files[ext] = mutableListOf(path)
            } else {
                ext2Files[ext]!! += path
            }
        }
        return ext2Files
    }

    private suspend fun hashClassify(pathList: List<String>): Map<Int, MutableList<File>> {
        val hash2File = mutableMapOf<Int, MutableList<File>>()
        for (path in pathList) {
            val file = File(path)
            if (file.exists() && file.canRead()) {
                val hash = IORepo.computeHash(file)
                val nullableList = hash2File[hash]
                if (nullableList != null) {
                    nullableList += file
                } else {
                    hash2File[hash] = mutableListOf(file)
                }
            } else {
                deleteFile(file.absolutePath)
            }
        }
        return hash2File
    }
}

class SnifferViewModelFactory(
    private val fileUriStr: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SnifferViewModel(fileUriStr) as T
    }
}