package com.andesearch.domain.usecase

import com.andesearch.data.local.FileEntry
import com.andesearch.data.repository.FileRepository
import com.andesearch.data.repository.SearchRepository
import com.andesearch.domain.index.PrefixTrie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchFilesUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val fileRepository: FileRepository,
    private val trie: PrefixTrie
) {
    operator fun invoke(query: String): Flow<List<FileEntry>> {
        if (query.isBlank()) return flowOf(emptyList())

        val sanitized = query.trim().lowercase()

        return flow {
            // Phase 1: Trie prefix match (in-memory, <5ms)
            val trieIds = trie.search(sanitized, limit = 100)
            val trieResults = if (trieIds.isNotEmpty()) {
                fileRepository.findByIds(trieIds)
            } else emptyList()

            // Phase 2: FTS full-text search (token-based, <30ms)
            val ftsResults = searchRepository.search(sanitized, limit = 200)

            // Phase 3: Contains fallback — LIKE '%query%' (only when prefix results are few)
            // Catches substrings that FTS tokenizer and Trie both miss
            val combined = linkedMapOf<Long, FileEntry>()
            trieResults.forEach { combined[it.id] = it }
            ftsResults.forEach { combined.putIfAbsent(it.id, it) }

            val containsResults = if (combined.size < 20) {
                searchRepository.containsSearch(sanitized, limit = 50)
            } else emptyList()

            // Phase 4: Merge, deduplicate, and rank
            val merged = mergeAndRank(sanitized, trieResults, ftsResults, containsResults)
            emit(merged)
        }
    }

    private fun mergeAndRank(
        query: String,
        trieResults: List<FileEntry>,
        ftsResults: List<FileEntry>,
        containsResults: List<FileEntry>
    ): List<FileEntry> {
        val scored = linkedMapOf<Long, Pair<FileEntry, Int>>()

        // Process Trie results (prefix hits from in-memory index)
        val trieIds = trieResults.map { it.id }.toSet()
        for (entry in trieResults) {
            var score = 10 // Trie prefix match baseline
            score += scoreEntry(query, entry)
            scored[entry.id] = entry to score
        }

        // Process FTS results (full-text hits from DB, may include pinyin matches)
        for (entry in ftsResults) {
            var score = scoreEntry(query, entry)
            if (score == 0) {
                score = 5 // FTS confirmed a match somewhere (name/path/pinyin/pinyinInitials)
            }
            val existing = scored[entry.id]
            if (existing != null) {
                if (score > existing.second) {
                    scored[entry.id] = entry to score
                }
            } else {
                scored[entry.id] = entry to score
            }
        }

        // Process contains fallback results (LIKE '%query%' — broadest match, lowest baseline)
        for (entry in containsResults) {
            var score = scoreEntry(query, entry)
            if (score == 0) {
                score = 3 // Contains fallback baseline — caught what Trie and FTS both missed
            }
            val existing = scored[entry.id]
            if (existing != null) {
                if (score > existing.second) {
                    scored[entry.id] = entry to score
                }
            } else {
                scored[entry.id] = entry to score
            }
        }

        // Sort by score descending, then Trie priority, then by name alphabetically
        return scored.values
            .sortedWith(
                compareByDescending<Pair<FileEntry, Int>> { it.second }
                    .thenBy { trieIds.contains(it.first.id).not() }
                    .thenBy { it.first.name.lowercase() }
            )
            .map { it.first }
    }

    private fun scoreEntry(query: String, entry: FileEntry): Int {
        var score = 0
        val name = entry.name.lowercase()
        val nameNoExt = name.substringBeforeLast('.')
        val path = entry.path.lowercase()
        val ext = entry.extension.lowercase()

        // 1. Exact name match (including without extension)
        if (name == query || nameNoExt == query) {
            score += 100
        }
        // 2. Name starts with query (prefix match)
        if (name.startsWith(query) || nameNoExt.startsWith(query)) {
            score += 50
        }
        // 3. Name contains query (substring match)
        if (name.contains(query)) {
            score += 30
        }
        // 4. Extension exact match
        if (ext == query) {
            score += 15
        }
        // 5. Path contains query (useful for folder names in path)
        if (path.contains(query)) {
            score += 10
        }

        // 6. Directory bonus
        if (entry.isDirectory) {
            score += 1
        }

        return score
    }
}
