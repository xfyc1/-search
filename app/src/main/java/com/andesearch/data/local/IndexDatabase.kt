package com.andesearch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FileEntry::class, FileEntryFts::class],
    version = 1,
    exportSchema = false
)
abstract class IndexDatabase : RoomDatabase() {
    abstract fun fileEntryDao(): FileEntryDao
}
