package com.andesearch.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "files",
    indices = [Index(value = ["path"], unique = true)]
)
data class FileEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val path: String,
    val parentPath: String,
    val extension: String,
    val size: Long,
    val mtime: Long,
    val isDirectory: Boolean,
    val isHidden: Boolean
)
