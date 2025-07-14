package com.d4rk.cleaner.app.widgets.domain.actions

import android.content.Context
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import com.d4rk.cleaner.core.data.datastore.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal fun Context.isWidgetActionsEnabled(): Boolean {
    val ds = DataStore(this)
    return runBlocking { ds.widgetActionsEnabled.first() }
}

internal fun GlanceModifier.scanAction() = clickable(actionRunCallback<OpenScanAction>())

internal fun GlanceModifier.cleanAction() = clickable(actionRunCallback<RunCleanAction>())
