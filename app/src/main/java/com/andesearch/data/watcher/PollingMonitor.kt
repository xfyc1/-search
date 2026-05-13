package com.andesearch.data.watcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollingMonitor @Inject constructor() {

    private val _changes = MutableSharedFlow<FileChangeEvent>(extraBufferCapacity = 64)
    val changes: SharedFlow<FileChangeEvent> = _changes

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val lastSnapshots = mutableMapOf<String, Long>()

    fun startPolling(dirs: List<String>, intervalMs: Long = 15 * 60 * 1000L) {
        job?.cancel()
        job = scope.launch {
            while (true) {
                dirs.forEach { dirPath ->
                    checkDirectory(File(dirPath))
                }
                delay(intervalMs)
            }
        }
    }

    fun stopPolling() {
        job?.cancel()
        lastSnapshots.clear()
    }

    private suspend fun checkDirectory(dir: File) {
        if (!dir.exists() || !dir.isDirectory) return

        val snapshotKey = dir.absolutePath
        val currentMtime = dir.lastModified()
        val currentCount = dir.listFiles()?.size ?: 0
        val currentHash = (currentMtime shl 32) or currentCount.toLong()

        val previous = lastSnapshots[snapshotKey]
        if (previous != null && previous != currentHash) {
            _changes.emit(FileChangeEvent.Modified(snapshotKey))
        }
        lastSnapshots[snapshotKey] = currentHash
    }
}
