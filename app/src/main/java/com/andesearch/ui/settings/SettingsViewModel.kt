package com.andesearch.ui.settings

import androidx.lifecycle.ViewModel
import com.andesearch.domain.index.IndexEngine
import com.andesearch.domain.model.IndexStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val indexEngine: IndexEngine
) : ViewModel() {

    val indexStatus: StateFlow<IndexStatus> = indexEngine.status

    fun rebuildIndex() {
        indexEngine.startIndexing()
    }

    fun stopIndexing() {
        indexEngine.stopIndexing()
    }
}
