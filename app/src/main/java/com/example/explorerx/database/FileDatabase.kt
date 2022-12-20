package com.example.explorerx.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.explorerx.model.googlechart.FileEntry

@Database(entities = [FileEntry::class], version = 1, exportSchema = false)
abstract class FileDatabase : RoomDatabase() {
    abstract fun fileEntryDao() : FileEntryDao
}