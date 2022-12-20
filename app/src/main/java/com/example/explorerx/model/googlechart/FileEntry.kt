package com.example.explorerx.model.googlechart

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.explorerx.utils.chart.quote

@Entity
data class FileEntry(
    @PrimaryKey val id: String,
    var parentId: String?,
    val size: Long,
) {

    override fun toString(): String {
        return "[\"$id\",${quote(parentId)},$size]"
    }
}