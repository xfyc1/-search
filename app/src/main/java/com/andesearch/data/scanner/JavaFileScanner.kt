package com.andesearch.data.scanner

import com.andesearch.data.local.FileEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class JavaFileScanner @Inject constructor() : FileScanner {

    override fun scan(rootPath: String): Flow<ScanProgress> = flow {
        val batch = mutableListOf<FileEntry>()
        var fileCount = 0
        var totalDirs = 0

        val rootFile = File(rootPath)
        if (!rootFile.exists() || !rootFile.isDirectory) {
            emit(ScanProgress.Error(rootPath, "根目录不存在或不是目录"))
            return@flow
        }

        if (!rootFile.canRead()) {
            emit(ScanProgress.Error(rootPath, "无法读取根目录"))
            return@flow
        }

        val dirs = ArrayDeque<File>()
        dirs.add(rootFile)

        while (dirs.isNotEmpty() && coroutineContext.isActive) {
            val dir = dirs.removeFirst()

            val files = try {
                dir.listFiles()
            } catch (e: SecurityException) {
                emit(ScanProgress.Error(dir.absolutePath, "权限不足"))
                continue
            } catch (e: Exception) {
                emit(ScanProgress.Error(dir.absolutePath, e.message ?: "未知错误"))
                continue
            }

            if (files == null) continue

            for (file in files) {
                if (!coroutineContext.isActive) return@flow

                val isDirectory = try {
                    file.isDirectory
                } catch (e: Exception) {
                    false
                }

                if (isDirectory) {
                    val name = file.name
                    if (!name.startsWith(".") || dir.absolutePath == rootPath) {
                        dirs.add(file)
                    }
                    totalDirs++
                }

                val entry = try {
                    fileToEntry(file)
                } catch (e: Exception) {
                    continue
                }

                if (entry != null) {
                    batch.add(entry)
                    fileCount++

                    if (batch.size >= BATCH_SIZE) {
                        val pct = (fileCount * 100 / (fileCount + dirs.size + 1)).coerceAtMost(99)
                        emit(ScanProgress.BatchComplete(batch.toList(), pct))
                        batch.clear()
                    }
                }
            }

            totalDirs--
            emit(ScanProgress.DirectoryScanned(dir.absolutePath, fileCount))
        }

        if (batch.isNotEmpty() && coroutineContext.isActive) {
            emit(ScanProgress.BatchComplete(batch.toList(), 100))
        }
        emit(ScanProgress.Complete)
    }.flowOn(Dispatchers.IO)

    private fun fileToEntry(file: File): FileEntry? {
        return try {
            val name = file.name
            val parent = file.parent ?: "/"
            val ext = if (file.isDirectory) "" else
                file.extension?.lowercase()?.take(32) ?: ""

            FileEntry(
                name = name,
                path = file.absolutePath,
                parentPath = parent,
                extension = ext,
                size = if (file.isFile) file.length() else 0L,
                mtime = file.lastModified(),
                isDirectory = file.isDirectory,
                isHidden = file.isHidden
            )
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val BATCH_SIZE = 500
    }
}
