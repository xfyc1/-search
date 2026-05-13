package com.andesearch.data.repository

import com.andesearch.data.local.FileEntry
import com.andesearch.data.local.FileEntryDao
import com.andesearch.data.local.FileEntryFts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val dao: FileEntryDao
) {
    suspend fun insert(entry: FileEntry): Long = dao.insert(entry)
    suspend fun insertFts(fts: FileEntryFts) = dao.insertFts(fts)
    suspend fun insertAll(entries: List<FileEntry>): List<Long> = dao.insertAll(entries)
    suspend fun insertAllFts(entries: List<FileEntryFts>) = dao.insertAllFts(entries)
    suspend fun findByIds(ids: List<Long>): List<FileEntry> = dao.findByIds(ids)
    suspend fun findByPath(path: String): FileEntry? = dao.findByPath(path)
    suspend fun findByParentPath(parentPath: String): List<FileEntry> =
        dao.findByParentPath(parentPath)
    suspend fun deleteByPath(path: String) {
        dao.deleteFtsByPath(path)
        dao.deleteByPath(path)
    }
    suspend fun deleteByParentPath(parentPath: String) {
        dao.deleteFtsByParentPath(parentPath)
        dao.deleteByParentPath(parentPath)
    }
    suspend fun deleteAll() {
        dao.deleteAllFts()
        dao.deleteAll()
    }
    suspend fun count(): Int = dao.count()
    fun countFlow(): Flow<Int> = dao.countFlow()
}
