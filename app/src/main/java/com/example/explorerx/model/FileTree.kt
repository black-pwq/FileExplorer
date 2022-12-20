package com.example.explorerx.model

import java.io.File

class FileTree(
    val file: File
) {
    val subTrees: MutableList<FileTree> = mutableListOf()
    var treeSize: Long = if (file.isFile) {
        file.length()
    } else {
        0
    }

    init {
        if (file.isDirectory && file.listFiles() != null) {
            for (f in file.listFiles()!!) {
                val subTree = FileTree(f)
                subTrees.plusAssign(subTree)
                treeSize += subTree.treeSize
            }
        }
    }
}
