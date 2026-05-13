package com.andesearch.di

import android.content.Context
import androidx.room.Room
import com.andesearch.data.local.FileEntryDao
import com.andesearch.data.local.IndexDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IndexDatabase {
        return Room.databaseBuilder(
            context,
            IndexDatabase::class.java,
            "andesearch.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFileEntryDao(db: IndexDatabase): FileEntryDao {
        return db.fileEntryDao()
    }
}
