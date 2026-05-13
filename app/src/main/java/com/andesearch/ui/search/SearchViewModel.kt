package com.andesearch.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andesearch.data.local.FileEntry
import com.andesearch.data.repository.FileRepository
import com.andesearch.domain.index.IndexEngine
import com.andesearch.domain.model.IndexStatus
import com.andesearch.domain.usecase.SearchFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchFilesUseCase: SearchFilesUseCase,
    private val indexEngine: IndexEngine,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<FileEntry>>(emptyList())
    val results: StateFlow<List<FileEntry>> = _results

    val indexStatus: StateFlow<IndexStatus> = indexEngine.status

    // Folder browsing state
    private val _browsingPath = MutableStateFlow<String?>(null)
    val browsingPath: StateFlow<String?> = _browsingPath

    private val _browsingEntries = MutableStateFlow<List<FileEntry>>(emptyList())
    val browsingEntries: StateFlow<List<FileEntry>> = _browsingEntries

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isBlank()) flowOf(emptyList())
                    else searchFilesUseCase(q)
                }
                .collect { entries ->
                    _results.value = entries
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun browseFolder(path: String) {
        viewModelScope.launch {
            _browsingPath.value = path
            _browsingEntries.value = fileRepository.findByParentPath(path)
                .sortedWith(compareByDescending<FileEntry> { it.isDirectory }.thenBy { it.name.lowercase() })
        }
    }

    fun goBackToSearch() {
        _browsingPath.value = null
        _browsingEntries.value = emptyList()
    }
}
