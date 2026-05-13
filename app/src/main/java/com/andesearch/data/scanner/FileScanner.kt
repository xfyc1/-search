package com.andesearch.data.scanner

import com.andesearch.data.local.FileEntry
import kotlinx.coroutines.flow.Flow

interface FileScanner {
    fun scan(rootPath: String): Flow<ScanProgress>
}

sealed interface ScanProgress {
    data class FileFound(val entry: FileEntry) : ScanProgress
    data class DirectoryScanned(val path: String, val fileCount: Int) : ScanProgress
    data class BatchComplete(val entries: List<FileEntry>, val progress: Int) : ScanProgress
    data class Error(val path: String, val message: String) : ScanProgress
    data object Complete : ScanProgress
}
