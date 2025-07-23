package com.d4rk.cleaner.core.utils.extensions

import android.database.sqlite.SQLiteException
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.cleaner.R
import com.d4rk.cleaner.core.domain.model.network.Errors
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.sql.SQLException

fun Errors.asUiText(): UiTextHelper {
    return when (this) {
        // Network errors
        Errors.Network.NO_INTERNET -> UiTextHelper.StringResource(R.string.request_no_internet)
        Errors.Network.REQUEST_TIMEOUT -> UiTextHelper.StringResource(R.string.request_timeout)
        Errors.Network.SERVER_ERROR -> UiTextHelper.StringResource(R.string.request_server_not_found)
        Errors.Network.SERIALIZATION -> UiTextHelper.StringResource(R.string.request_payload_error)
        else -> {
            UiTextHelper.StringResource(com.d4rk.android.libs.apptoolkit.R.string.unknown_error)
        }
    }
}

fun Throwable.toError(default: Errors = Errors.UseCase.NO_DATA): Errors {
    return when (this) {
        is UnknownHostException -> Errors.Network.NO_INTERNET
        is SocketTimeoutException -> Errors.Network.REQUEST_TIMEOUT
        is ConnectException -> Errors.Network.NO_INTERNET
        is SerializationException -> Errors.Network.SERIALIZATION
        is SQLException, is SQLiteException -> Errors.Database.DATABASE_OPERATION_FAILED
        is IllegalStateException -> when (this.message) {
            else -> Errors.UseCase.FAILED_TO_ENCRYPT_CART
        }

        else -> default
    }
}