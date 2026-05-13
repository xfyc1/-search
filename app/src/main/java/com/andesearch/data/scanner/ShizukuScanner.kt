package com.andesearch.data.scanner

import com.andesearch.data.local.FileEntry
import kotlinx.coroutines.flow.Flow

// Placeholder for Shizuku-based scanner.
// Shizuku allows accessing restricted directories (Android/data, etc.)
// without root by running as a privileged process via ADB/wireless debugging.
// Will be implemented in Phase 5.
class ShizukuScanner : FileScanner {
    override fun scan(rootPath: String): Flow<ScanProgress> {
        // To be implemented with Shizuku UserService
        TODO("Shizuku integration not yet implemented")
    }
}
