package com.example.explorerx.repository

import android.content.Context
import androidx.room.Room
import com.example.explorerx.database.FileDatabase
import com.example.explorerx.model.googlechart.FileEntry
import kotlinx.coroutines.flow.Flow

private const val DATABASE_NAME = "fileentry-database"

class FileEntryRepo private constructor(context: Context) {
    private val database = Room
        .databaseBuilder(
            context.applicationContext,
            FileDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    fun loadAll(): Flow<List<FileEntry>> = database.fileEntryDao().loadAll()

    suspend fun findByPrefix(prefix: String) = database.fileEntryDao().findByPrefix(prefix)

    suspend fun loadById(id: String) = database.fileEntryDao().loadById(id)

    suspend fun deleteAll() = database.fileEntryDao().deleteAll()

    suspend fun getFileEntry(id: String) = database.fileEntryDao().loadById(id)

    suspend fun insertFileEntries(list: List<FileEntry>) = database.fileEntryDao().insertAll(list)

    suspend fun insertFileEntry(fileEntry: FileEntry) = database.fileEntryDao().insert(fileEntry)

    suspend fun deleteByPrefix(prefix: String) = database.fileEntryDao().deleteByPrefix(prefix)

    suspend fun deleteById(id: String) = database.fileEntryDao().deleteById(id)

    fun findFilesWithSameSizeByPrefix(prefix: String) =
        database.fileEntryDao().sameSizeListByPrefix(prefix)

    companion object {
        private var INSTANCE: FileEntryRepo? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = FileEntryRepo(context)
            }
        }

        fun get(): FileEntryRepo {
            return INSTANCE
                ?: throw IllegalStateException("com.example.explorerx.repository.FileEntryRepo must be initialized")
        }
    }
}