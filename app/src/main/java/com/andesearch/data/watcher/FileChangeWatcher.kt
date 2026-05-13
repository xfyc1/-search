package com.andesearch.data.watcher

import android.os.Environment
import com.andesearch.data.local.FileEntry
import com.andesearch.data.repository.FileRepository
import com.andesearch.domain.index.PinyinMapper
import com.andesearch.domain.index.PrefixTrie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileChangeWatcher @Inject constructor(
    private val observer: RecursiveObserver,
    private val polling: PollingMonitor,
    private val repository: FileRepository,
    private val trie: PrefixTrie,
    private val pinyinMapper: PinyinMapper
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val highFreqDirs = listOf(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath,
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
    ).filter { File(it).exists() }

    fun start() {
        observer.startWatching(highFreqDirs) { event ->
            scope.launch { handleEvent(event) }
        }

        // Poll remaining storage every 15 minutes
        val root = "/storage/emulated/0"
        polling.startPolling(listOf(root))
        scope.launch {
            polling.changes.collect { event ->
                handleEvent(event)
            }
        }
    }

    fun stop() {
        observer.stopWatching()
        polling.stopPolling()
    }

    private suspend fun handleEvent(event: FileChangeEvent) {
        when (event) {
            is FileChangeEvent.Created -> onFileCreated(event.path)
            is FileChangeEvent.Deleted -> onFileDeleted(event.path)
            is FileChangeEvent.Modified -> onFileModified(event.path)
        }
    }

    private suspend fun onFileCreated(path: String) {
        val file = File(path)
        if (!file.exists()) return

        val entry = FileEntry(
            name = file.name,
            path = file.absolutePath,
            parentPath = file.parent ?: "/",
            extension = file.extension?.lowercase()?.take(32) ?: "",
            size = if (file.isFile) file.length() else 0L,
            mtime = file.lastModified(),
            isDirectory = file.isDirectory,
            isHidden = file.isHidden
        )

        val fileId = repository.insert(entry)
        trie.insert(entry.name, fileId)

        val pinyin = pinyinMapper.toPinyin(entry.name)
        if (pinyin.isNotEmpty()) {
            trie.insert(pinyin, fileId)
            trie.insert(pinyinMapper.toInitials(entry.name), fileId)
        }
    }

    private suspend fun onFileDeleted(path: String) {
        val existing = repository.findByPath(path)
        if (existing != null) {
            trie.remove(existing.name, existing.id)
            val pinyin = pinyinMapper.toPinyin(existing.name)
            if (pinyin.isNotEmpty()) {
                trie.remove(pinyin, existing.id)
                trie.remove(pinyinMapper.toInitials(existing.name), existing.id)
            }
        }
        repository.deleteByPath(path)
    }

    private suspend fun onFileModified(path: String) {
        // For modification, re-scan the file to update metadata
        onFileDeleted(path)
        onFileCreated(path)
    }
}
