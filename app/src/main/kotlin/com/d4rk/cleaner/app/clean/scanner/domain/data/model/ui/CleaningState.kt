package com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui

/**
 * Represents the different phases of the cleaning flow. Each state should
 * only transition in the expected order to prevent overlapping operations.
 */
enum class CleaningState {
    Idle,
    Analyzing,

    /**
     * Files have been analyzed and the user can review the results.
     */
    ReadyToClean,

    /**
     * Deleting or moving the selected files is in progress.
     */
    Cleaning,

    /**
     * Cleaning finished and a short summary/result should be shown.
     */
    Result,

    Error
}
