package com.d4rk.cleaner.app.images.picker.domain.usecases

import kotlinx.coroutines.delay

class ShowFabUseCase {
    suspend operator fun invoke(delayMillis: Long = 100L) {
        delay(delayMillis)
    }
}
