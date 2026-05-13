package com.andesearch.data.repository

import com.andesearch.data.local.FileEntry
import com.andesearch.data.local.FileEntryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val dao: FileEntryDao
) {
    suspend fun search(query: String, limit: Int = 200): List<FileEntry> {
        if (query.isBlank()) return emptyList()
        val sanitized = query
            .replace("\"", "")
            .replace("*", "")
            .trim()
        if (sanitized.isEmpty()) return emptyList()
        return dao.searchFts("$sanitized*", limit)
    }

    fun searchFlow(query: String, limit: Int = 200): Flow<List<FileEntry>> {
        if (query.isBlank()) return flowOf(emptyList())
        val sanitized = query
            .replace("\"", "")
            .replace("*", "")
            .trim()
        return dao.searchFtsFlow("$sanitized*", limit)
    }

    suspend fun prefixSearch(prefix: String, limit: Int = 20): List<FileEntry> {
        return dao.prefixSearch(prefix, limit)
    }

    suspend fun containsSearch(query: String, limit: Int = 50): List<FileEntry> {
        return dao.containsSearch(query, limit)
    }
}
