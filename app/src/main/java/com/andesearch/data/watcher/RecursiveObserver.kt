package com.andesearch.data.watcher

import android.os.FileObserver
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecursiveObserver @Inject constructor() {

    private val observers = mutableMapOf<String, FileObserver>()
    private var onChangeCallback: ((FileChangeEvent) -> Unit)? = null

    fun startWatching(
        dirs: List<String>,
        onEvent: (FileChangeEvent) -> Unit
    ) {
        onChangeCallback = onEvent
        dirs.forEach { dir ->
            watchRecursive(File(dir))
        }
    }

    private fun watchRecursive(dir: File) {
        if (!dir.exists() || !dir.isDirectory) return
        if (observers.containsKey(dir.absolutePath)) return

        val observer = object : FileObserver(dir, ALL_EVENTS) {
            override fun onEvent(event: Int, path: String?) {
                if (path == null) return
                val fullPath = "${dir.absolutePath}/$path"
                when {
                    (event and CREATE) != 0 -> onChangeCallback?.invoke(
                        FileChangeEvent.Created(fullPath)
                    )
                    (event and DELETE) != 0 -> onChangeCallback?.invoke(
                        FileChangeEvent.Deleted(fullPath)
                    )
                    (event and MOVED_FROM) != 0 -> onChangeCallback?.invoke(
                        FileChangeEvent.Deleted(fullPath)
                    )
                    (event and MOVED_TO) != 0 -> onChangeCallback?.invoke(
                        FileChangeEvent.Created(fullPath)
                    )
                    (event and MODIFY) != 0 -> {
                        // Only care about metadata changes, not content
                        onChangeCallback?.invoke(
                            FileChangeEvent.Modified(fullPath)
                        )
                    }
                }
            }
        }

        try {
            observer.startWatching()
            observers[dir.absolutePath] = observer
        } catch (_: Exception) {
            // FileObserver limit reached, skip this dir
        }

        // Recurse one level deep for high-value subdirs
        if (observers.size < 200) {
            dir.listFiles()?.filter { it.isDirectory && !it.name.startsWith(".") }
                ?.take(5)
                ?.forEach { watchRecursive(it) }
        }
    }

    fun stopWatching() {
        observers.values.forEach { it.stopWatching() }
        observers.clear()
    }

    companion object {
        val ALL_EVENTS = FileObserver.CREATE or FileObserver.DELETE or
            FileObserver.MOVED_FROM or FileObserver.MOVED_TO or FileObserver.MODIFY
    }
}

sealed interface FileChangeEvent {
    data class Created(val path: String) : FileChangeEvent
    data class Deleted(val path: String) : FileChangeEvent
    data class Modified(val path: String) : FileChangeEvent
}
