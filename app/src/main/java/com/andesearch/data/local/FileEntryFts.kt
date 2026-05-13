package com.andesearch.data.local

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Entity(tableName = "files_fts")
@Fts4(tokenizer = FtsOptions.TOKENIZER_UNICODE61)
data class FileEntryFts(
    val fileId: Long,
    val name: String,
    val path: String,
    val pinyin: String,
    val pinyinInitials: String
)
