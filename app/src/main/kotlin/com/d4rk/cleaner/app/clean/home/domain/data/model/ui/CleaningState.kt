package com.d4rk.cleaner.app.clean.home.domain.data.model.ui

/**
 * Represents the different phases of the cleaning flow. Each state should
 * only transition in the expected order to prevent overlapping operations.
 */
enum class CleaningState {
    Idle,
    Analyzing,
    Cleaning,
    Success,
    Error
}
