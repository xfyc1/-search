package com.andesearch.domain.usecase

import com.andesearch.data.local.FileEntry
import com.andesearch.data.repository.FileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFileDetailsUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(path: String): FileEntry? {
        return repository.findByPath(path)
    }
}
