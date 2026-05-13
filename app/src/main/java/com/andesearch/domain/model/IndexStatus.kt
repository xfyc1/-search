package com.andesearch.domain.model

sealed interface IndexStatus {
    data object Idle : IndexStatus
    data class Scanning(val current: Int, val total: Int, val currentPath: String) : IndexStatus
    data class Building(val phase: String) : IndexStatus
    data object Complete : IndexStatus
    data class Error(val message: String) : IndexStatus
}
