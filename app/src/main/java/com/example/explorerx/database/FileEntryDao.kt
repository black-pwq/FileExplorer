package com.example.explorerx.database

import androidx.room.*
import com.example.explorerx.model.googlechart.FileEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FileEntryDao {
    @Query("select * from fileentry")
    fun loadAll(): Flow<List<FileEntry>>

    @Transaction
    @Query("delete from fileentry")
    suspend fun deleteAll()

    @Transaction
    @Query("delete from fileentry where id = :id")
    suspend fun deleteById(id: String)

    @Query("select * from fileentry where id = (:id) ")
    suspend fun loadById(id: String): FileEntry?

    @Query("select id from fileentry where size = (:size) and id not like '%/.%'")
    suspend fun findBySize(size: Long) : List<String>

    @Query("select * from fileentry where id like (:prefix || '%')")
    suspend fun findByPrefix(prefix: String): List<FileEntry>

    @Transaction
    @Query("delete from fileentry where id like (:prefix || '%')")
    suspend fun deleteByPrefix(prefix: String)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fileEntries: FileEntry)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fileEntries: List<FileEntry>)

    @Query("select size from fileentry where size > 512 and id not like '%/.%' group by size having count(size) > 1")
    suspend fun sameSizeList(): List<Long>

    @MapInfo(keyColumn = "size", valueColumn = "id")
    @Query("select size, id from fileentry where id not like '%/.%' and size in (select size from fileentry where size > 0 and id like (:prefix || '%') and id not like '%/.%' group by size having count(size) > 1)")
    fun sameSizeListByPrefix(prefix: String): Flow<Map<Long, List<String>>>
}