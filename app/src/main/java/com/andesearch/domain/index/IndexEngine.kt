package com.andesearch.domain.index

import com.andesearch.data.local.FileEntry
import com.andesearch.data.local.FileEntryFts
import com.andesearch.data.repository.FileRepository
import com.andesearch.data.scanner.FileScanner
import com.andesearch.data.scanner.ScanProgress
import com.andesearch.domain.model.IndexStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IndexEngine @Inject constructor(
    private val scanner: FileScanner,
    private val fileRepository: FileRepository,
    private val pinyinMapper: PinyinMapper,
    private val trie: PrefixTrie
) {
    private val _status = MutableStateFlow<IndexStatus>(IndexStatus.Idle)
    val status: StateFlow<IndexStatus> = _status

    private var indexJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startIndexing(rootPaths: List<String> = listOf("/storage/emulated/0")) {
        if (indexJob?.isActive == true) return

        indexJob = scope.launch {
            _status.value = IndexStatus.Building("正在清除旧索引…")
            fileRepository.deleteAll()
            trie.clear()

            var totalFiles = 0

            for (root in rootPaths) {
                scanner.scan(root).collect { progress ->
                    when (progress) {
                        is ScanProgress.BatchComplete -> {
                            insertBatch(progress.entries)
                            totalFiles += progress.entries.size
                            _status.value = IndexStatus.Scanning(
                                current = totalFiles,
                                total = totalFiles + 1,
                                currentPath = progress.entries.lastOrNull()?.path ?: ""
                            )
                        }
                        is ScanProgress.DirectoryScanned -> {}
                        is ScanProgress.Error -> {}
                        is ScanProgress.FileFound -> {}
                        is ScanProgress.Complete -> {}
                    }
                }
            }

            _status.value = IndexStatus.Complete
        }
    }

    fun stopIndexing() {
        indexJob?.cancel()
        _status.value = IndexStatus.Idle
    }

    private suspend fun insertBatch(entries: List<FileEntry>) {
        // Batch insert all file entries, get back generated IDs
        val ids = fileRepository.insertAll(entries)

        val ftsEntries = mutableListOf<FileEntryFts>()

        for ((index, entry) in entries.withIndex()) {
            val fileId = ids[index]
            trie.insert(entry.name, fileId)

            val pinyin = pinyinMapper.toPinyin(entry.name)
            val initials = pinyinMapper.toInitials(entry.name)
            if (pinyin.isNotEmpty()) {
                trie.insert(pinyin, fileId)
                trie.insert(initials, fileId)
            }

            ftsEntries.add(
                FileEntryFts(
                    fileId = fileId,
                    name = entry.name,
                    path = entry.path,
                    pinyin = pinyin,
                    pinyinInitials = initials
                )
            )
        }

        // Batch insert all FTS entries
        fileRepository.insertAllFts(ftsEntries)
    }
}
