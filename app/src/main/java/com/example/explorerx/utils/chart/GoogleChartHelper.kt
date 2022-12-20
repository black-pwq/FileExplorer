package com.example.explorerx.utils.chart

import com.example.explorerx.model.googlechart.FileEntry

fun quote(str: String?): String {
    if (str == null)
        return "null"
    return "\"$str\""
}

fun fillRawHtml(rawHtml: String, header: String, data: List<FileEntry>): String {
    return String.format(
        rawHtml,
        header,
        data.joinToString(separator = ",\n")
    )
}