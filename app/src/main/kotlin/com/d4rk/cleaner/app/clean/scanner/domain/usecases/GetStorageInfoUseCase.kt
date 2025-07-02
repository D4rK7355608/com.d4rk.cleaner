package com.d4rk.cleaner.app.clean.scanner.domain.usecases

import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.cleaner.app.clean.scanner.domain.data.model.ui.UiScannerModel
import com.d4rk.cleaner.app.clean.scanner.domain.`interface`.ScannerRepositoryInterface
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetStorageInfoUseCase(private val homeRepository : ScannerRepositoryInterface) {
    operator fun invoke() : Flow<DataState<UiScannerModel , Errors>> = flow {
        runCatching {
            val storageInfo = homeRepository.getStorageInfo() // Call suspending repository function
            emit(DataState.Success(data = storageInfo))
        }.onFailure { e ->
            // emit(DataState.Error(error = e.message.toString()))
        }
    }
}