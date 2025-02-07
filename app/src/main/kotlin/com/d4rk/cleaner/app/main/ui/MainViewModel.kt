package com.d4rk.cleaner.app.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.app.main.domain.updates.CheckForUpdatesUseCase
import com.d4rk.cleaner.core.domain.onError
import com.d4rk.cleaner.core.domain.onSuccess
import kotlinx.coroutines.launch

class MainViewModel(private val checkForUpdatesUseCase: CheckForUpdatesUseCase) : ViewModel() {

    fun checkForUpdates() {
        viewModelScope.launch {
            checkForUpdatesUseCase.execute()
                    .onSuccess { isUpdateAvailable ->
                        if (isUpdateAvailable) {
                            // Afișăm notificarea de actualizare
                        }
                    }
                    .onError { error ->
                        // Gestionează eroarea
                    }
        }
    }
}
