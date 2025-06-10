package com.d4rk.cleaner.app.apps.manager.domain.actions

import android.content.Intent
import com.d4rk.android.libs.apptoolkit.core.ui.base.handling.ActionEvent

sealed class AppManagerAction : ActionEvent {
    data class LaunchShareIntent(val intent : Intent) : AppManagerAction()
}