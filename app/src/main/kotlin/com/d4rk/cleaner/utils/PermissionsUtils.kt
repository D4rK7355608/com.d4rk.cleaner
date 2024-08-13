package com.d4rk.cleaner.utils

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.d4rk.cleaner.constants.permissions.PermissionsConstants

/**
 * Utility class for handling runtime permissions.
 */
object PermissionsUtils {

    /**
     * Checks if the app has all necessary storage permissions.
     *
     * @param context The application context.
     * @return True if all required permissions are granted, false otherwise.
     */
    fun hasStoragePermissions(context: Context): Boolean {
        val hasStoragePermissions: Boolean = when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 -> ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            else -> true
        }

        val hasManageStoragePermission: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                true
            }

        return hasStoragePermissions && hasManageStoragePermission
    }

    /**
     * Requests the necessary storage permissions.
     *
     * @param activity The Activity instance required to request permissions.
     */
    fun requestStoragePermissions(activity: Activity) {
        val requiredPermissions: MutableList<String> = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }

        }

        ActivityCompat.requestPermissions(
            activity,
            requiredPermissions.toTypedArray(),
            PermissionsConstants.REQUEST_CODE_STORAGE_PERMISSIONS
        )
    }

    fun hasMediaPermissions(context: Context): Boolean {
        val hasMediaPermissions: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        return hasMediaPermissions
    }

    fun requestMediaPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requiredPermissions: List<String> = listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )

            ActivityCompat.requestPermissions(
                activity,
                requiredPermissions.toTypedArray(),
                PermissionsConstants.REQUEST_CODE_STORAGE_PERMISSIONS
            )
        }
    }

    fun hasUsageAccessPermissions(context: Context): Boolean {
        val hasUsageStatsPermission: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                isAccessGranted(context)
            } else {
                true
            }

        return hasUsageStatsPermission
    }

    fun requestUsageAccess(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isAccessGranted(activity)) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                activity.startActivity(intent)
            }
        }
    }

    /**
     * Checks if the app has access to usage statistics.
     *
     * @param context The application context.
     * @return True if access is granted, false otherwise.
     */
    private fun isAccessGranted(context: Context): Boolean = try {
        val packageManager: PackageManager = context.packageManager
        val applicationInfo: ApplicationInfo =
            packageManager.getApplicationInfo(context.packageName, 0)
        val appOpsManager: AppOpsManager =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        @Suppress("DEPRECATION") val mode: Int = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName
        )
        mode == AppOpsManager.MODE_ALLOWED
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }


    /**
     * Checks if the app has permission to post notifications.
     *
     * @param context The application context.
     * @return True if the permission is granted, false otherwise.
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Requests the notification permission.
     *
     * @param activity The Activity instance required to request the permission.
     */
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PermissionsConstants.REQUEST_CODE_NOTIFICATION_PERMISSION
            )
        }
    }
}