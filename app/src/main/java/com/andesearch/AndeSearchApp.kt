package com.andesearch

import android.app.Application
import android.os.Environment
import android.util.Log
import com.andesearch.data.repository.FileRepository
import com.andesearch.data.watcher.FileChangeWatcher
import com.andesearch.domain.index.IndexEngine
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AndeSearchApp : Application() {

    @Inject lateinit var indexEngine: IndexEngine
    @Inject lateinit var fileWatcher: FileChangeWatcher
    @Inject lateinit var fileRepository: FileRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        try {
            if (!Environment.isExternalStorageManager()) return

            scope.launch {
                try {
                    val count = fileRepository.count()
                    if (count == 0) {
                        indexEngine.startIndexing()
                    }
                    fileWatcher.start()
                } catch (e: Exception) {
                    Log.e(TAG, "Index init failed", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "App init failed", e)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        try {
            fileWatcher.stop()
        } catch (_: Exception) {}
    }

    companion object {
        private const val TAG = "AndeSearch"
    }
}
