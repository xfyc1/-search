package com.andesearch.domain.model

data class FileInfo(
    val id: Long,
    val name: String,
    val path: String,
    val parentPath: String,
    val extension: String,
    val size: Long,
    val mtime: Long,
    val isDirectory: Boolean,
    val isHidden: Boolean
) {
    val formattedSize: String
        get() = when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
}
