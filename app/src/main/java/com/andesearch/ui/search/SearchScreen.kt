package com.andesearch.ui.search

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.andesearch.data.local.FileEntry
import com.andesearch.domain.model.IndexStatus
import com.andesearch.ui.components.IndexProgressBar
import com.andesearch.ui.components.PermissionGate
import com.andesearch.ui.components.SearchBar
import com.andesearch.ui.results.ResultsList
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val indexStatus by viewModel.indexStatus.collectAsState()
    val browsingPath by viewModel.browsingPath.collectAsState()
    val browsingEntries by viewModel.browsingEntries.collectAsState()
    val context = LocalContext.current
    val focusRequester = FocusRequester()

    val isBrowsing = browsingPath != null
    val showEmptyState = !isBrowsing && query.isBlank()
    val showNoResults = !isBrowsing && query.isNotBlank() && results.isEmpty()

    PermissionGate {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                if (isBrowsing) {
                    TopAppBar(
                        title = {
                            Text(
                                text = browsingPath ?: "",
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.goBackToSearch() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "返回搜索"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                } else {
                    TopAppBar(
                        title = {
                            Text(
                                text = "文件搜索",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = {
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "设置",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }

                // Search bar (hidden when browsing folder)
                if (!isBrowsing) {
                    SearchBar(
                        query = query,
                        onQueryChange = viewModel::onQueryChange,
                        focusRequester = focusRequester
                    )
                }

                // Index progress
                IndexProgressBar(status = indexStatus)

                // Content area
                if (isBrowsing) {
                    ResultsList(
                        entries = browsingEntries,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        onItemClick = { entry ->
                            if (entry.isDirectory) {
                                viewModel.browseFolder(entry.path)
                            } else {
                                openFile(context, entry)
                            }
                        }
                    )
                } else {
                    SearchContentArea(
                        showEmptyState = showEmptyState,
                        showNoResults = showNoResults,
                        showResults = results.isNotEmpty(),
                        isIndexing = indexStatus is IndexStatus.Scanning ||
                                indexStatus is IndexStatus.Building,
                        query = query,
                        results = results,
                        onItemClick = { entry ->
                            if (entry.isDirectory) {
                                viewModel.browseFolder(entry.path)
                            } else {
                                openFile(context, entry)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun openFile(context: android.content.Context, entry: FileEntry) {
    val file = File(entry.path)
    if (file.exists()) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(entry.extension))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "无法打开文件", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun EmptySearchState(isIndexing: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isIndexing) "正在建立索引…" else "输入文件名开始搜索",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isIndexing) "索引完成后即可搜索所有文件" else "支持拼音搜索，如输入 \"bg\" 查找 \"报告\"",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NoResultsState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "未找到 \"$query\"",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "尝试其他关键词或检查拼写",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SearchContentArea(
    showEmptyState: Boolean,
    showNoResults: Boolean,
    showResults: Boolean,
    isIndexing: Boolean,
    query: String,
    results: List<FileEntry>,
    onItemClick: (FileEntry) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showEmptyState,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptySearchState(isIndexing = isIndexing)
        }

        AnimatedVisibility(
            visible = showNoResults,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            NoResultsState(query = query)
        }

        AnimatedVisibility(
            visible = showResults,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ResultsList(
                entries = results,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                onItemClick = onItemClick
            )
        }
    }
}

private fun getMimeType(extension: String): String = when (extension) {
    "pdf" -> "application/pdf"
    "png" -> "image/png"
    "jpg", "jpeg" -> "image/jpeg"
    "gif" -> "image/gif"
    "mp3" -> "audio/mpeg"
    "mp4" -> "video/mp4"
    "txt" -> "text/plain"
    "html" -> "text/html"
    "zip" -> "application/zip"
    "apk" -> "application/vnd.android.package-archive"
    else -> "*/*"
}
