package com.andesearch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FileEntryDao {

    // FTS search — join FTS back to files via fileId
    // FTS4 does not have a rank column; sort by name for stable results
    @Query("""
        SELECT f.* FROM files f
        INNER JOIN files_fts ft ON f.id = ft.fileId
        WHERE files_fts MATCH :query
        ORDER BY f.name ASC
        LIMIT :limit
    """)
    suspend fun searchFts(query: String, limit: Int = 200): List<FileEntry>

    @Query("""
        SELECT f.* FROM files f
        INNER JOIN files_fts ft ON f.id = ft.fileId
        WHERE files_fts MATCH :query
        ORDER BY f.name ASC
        LIMIT :limit
    """)
    fun searchFtsFlow(query: String, limit: Int = 200): Flow<List<FileEntry>>

    // Prefix fallback
    @Query("SELECT * FROM files WHERE name LIKE :prefix || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun prefixSearch(prefix: String, limit: Int = 20): List<FileEntry>

    // Contains fallback — catches substrings that FTS prefix/token match misses
    @Query("SELECT * FROM files WHERE name LIKE '%' || :query || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun containsSearch(query: String, limit: Int = 50): List<FileEntry>

    @Query("SELECT * FROM files WHERE id IN (:ids)")
    suspend fun findByIds(ids: List<Long>): List<FileEntry>

    @Query("SELECT * FROM files WHERE path = :path LIMIT 1")
    suspend fun findByPath(path: String): FileEntry?

    @Query("SELECT * FROM files WHERE parentPath = :parentPath")
    suspend fun findByParentPath(parentPath: String): List<FileEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FileEntry>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FileEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFts(entries: List<FileEntryFts>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFts(entry: FileEntryFts)

    @Query("DELETE FROM files WHERE path = :path")
    suspend fun deleteByPath(path: String)

    @Query("DELETE FROM files_fts WHERE fileId = (SELECT id FROM files WHERE path = :path)")
    suspend fun deleteFtsByPath(path: String)

    @Query("DELETE FROM files WHERE path LIKE :parentPath || '/%'")
    suspend fun deleteByParentPath(parentPath: String)

    @Query("DELETE FROM files_fts WHERE fileId IN (SELECT id FROM files WHERE path LIKE :parentPath || '/%')")
    suspend fun deleteFtsByParentPath(parentPath: String)

    @Query("DELETE FROM files")
    suspend fun deleteAll()

    @Query("DELETE FROM files_fts")
    suspend fun deleteAllFts()

    @Query("SELECT COUNT(*) FROM files")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM files")
    fun countFlow(): Flow<Int>
}
