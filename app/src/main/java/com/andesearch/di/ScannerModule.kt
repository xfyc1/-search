package com.andesearch.di

import com.andesearch.data.scanner.FileScanner
import com.andesearch.data.scanner.JavaFileScanner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerModule {

    @Binds
    @Singleton
    abstract fun bindFileScanner(impl: JavaFileScanner): FileScanner
}
