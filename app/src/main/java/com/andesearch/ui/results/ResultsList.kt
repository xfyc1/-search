package com.andesearch.ui.results

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andesearch.data.local.FileEntry

@Composable
fun ResultsList(
    entries: List<FileEntry>,
    modifier: Modifier = Modifier,
    onItemClick: (FileEntry) -> Unit = {},
    onItemLongClick: (FileEntry) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(entries, key = { it.id }) { entry ->
            ResultItem(
                entry = entry,
                onClick = { onItemClick(entry) },
                onLongClick = { onItemLongClick(entry) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ResultItem(
    entry: FileEntry,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val fileType = getFileType(entry)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // File type icon with background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(fileType.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fileType.icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = fileType.color
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and path
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = entry.parentPath,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // File size or folder indicator
        if (entry.isDirectory) {
            Text(
                text = "›",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
            )
        } else {
            Text(
                text = formatSize(entry.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

private data class FileType(
    val icon: ImageVector,
    val color: Color
)

private fun getFileType(entry: FileEntry): FileType = when {
    entry.isDirectory -> FileType(Icons.Default.Folder, Color(0xFF5C9CE5))
    entry.extension in setOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic") ->
        FileType(Icons.Default.Image, Color(0xFF4CAF50))
    entry.extension in setOf("mp3", "wav", "flac", "aac", "ogg", "m4a") ->
        FileType(Icons.Default.MusicNote, Color(0xFFE91E63))
    entry.extension in setOf("mp4", "mkv", "avi", "mov", "webm", "3gp") ->
        FileType(Icons.Default.Videocam, Color(0xFF9C27B0))
    entry.extension == "pdf" ->
        FileType(Icons.Default.PictureAsPdf, Color(0xFFF44336))
    entry.extension in setOf("txt", "md", "xml", "json", "csv", "log", "html", "css", "js") ->
        FileType(Icons.Default.Description, Color(0xFF607D8B))
    entry.extension in setOf("kt", "java", "py", "cpp", "c", "go", "rs", "swift") ->
        FileType(Icons.Default.Code, Color(0xFFFF9800))
    else -> FileType(Icons.AutoMirrored.Filled.InsertDriveFile, Color(0xFF78909C))
}

private fun formatSize(bytes: Long): String = when {
    bytes <= 0 -> ""
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
    else -> "${bytes / (1024 * 1024 * 1024)} GB"
}
