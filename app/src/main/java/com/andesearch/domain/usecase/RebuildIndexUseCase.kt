package com.andesearch.domain.usecase

import com.andesearch.domain.index.IndexEngine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RebuildIndexUseCase @Inject constructor(
    private val indexEngine: IndexEngine
) {
    operator fun invoke(rootPaths: List<String> = listOf("/storage/emulated/0")) {
        indexEngine.startIndexing(rootPaths)
    }
}
