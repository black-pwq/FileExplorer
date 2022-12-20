package com.example.explorerx.utils

import com.example.explorerx.model.FileTree
import java.io.File

private val sizeUnits = arrayOf("B", "KB", "MB", "GB")

fun FileTree.readableSize(): String {
    var size = this.treeSize
    var index = 0
    while ((size shr 10) > 0) {
        size = size shr 10
        index++
    }
    return "$size ${sizeUnits[index]}"
}

fun FileTree.sizeLevel(): Int {
    return sizeUnitLevel(this.treeSize)
}

fun File.readableSize(): String {
    var size = this.length()
    var index = 0
    while ((size shr 10) > 0) {
        size = size shr 10
        index++
    }
    return "$size ${sizeUnits[index]}"
}

fun Long.readableSize(): String {
    var size = this
    var index = 0
    while ((size shr 10) > 0) {
        size = size shr 10
        index++
    }
    return "$size ${sizeUnits[index]}"
}

private fun sizeUnitLevel(length: Long): Int {
    var size = length
    var index = 0
    while ((size shr 10) > 0) {
        size = size shr 10
        index++
    }
    return index
}

private fun sizeUnit(length: Long): String {
    return sizeUnits[sizeUnitLevel(length)]
}