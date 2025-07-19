package com.d4rk.cleaner.core.utils.helpers

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
import com.d4rk.cleaner.core.utils.constants.permissions.AppPermissionsConstants

/**
 * Utility class for handling runtime permissions.
 */
object PermissionsHelper {

    /**
     * Checks if the app has all necessary storage permissions.
     *
     * @param context The application context.
     * @return True if all required permissions are granted, false otherwise.
     */
    fun hasStoragePermissions(context : Context) : Boolean {
        val hasStoragePermissions : Boolean = when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> ContextCompat.checkSelfPermission(
                context , Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 -> ContextCompat.checkSelfPermission(
                context , Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            else -> true
        }

        val hasManageStoragePermission : Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        }
        else {
            true
        }

        return hasStoragePermissions && hasManageStoragePermission
    }

    /**
     * Requests the necessary storage permissions.
     *
     * @param activity The Activity instance required to request permissions.
     */
    fun requestStoragePermissions(activity : Activity) {
        val requiredPermissions : MutableList<String> = mutableListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (! Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri : Uri = Uri.fromParts("package" , activity.packageName , null)
                intent.data = uri
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

                val packageManager : PackageManager = activity.packageManager
                if (intent.resolveActivity(packageManager) != null) {
                    runCatching { activity.startActivity(intent) }
                }
                else {
                    val fallbackIntent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS , uri
                    )
                    fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    if (fallbackIntent.resolveActivity(packageManager) != null) {
                        runCatching { activity.startActivity(fallbackIntent) }
                    }
                }
            }

        }

        ActivityCompat.requestPermissions(
            activity , requiredPermissions.toTypedArray() , AppPermissionsConstants.REQUEST_CODE_STORAGE_PERMISSIONS
        )
    }

    fun hasUsageAccessPermissions(context : Context) : Boolean {
        val hasUsageStatsPermission : Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isAccessGranted(context)
        }
        else {
            true
        }

        return hasUsageStatsPermission
    }

    fun requestUsageAccess(activity : Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (! isAccessGranted(activity)) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
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
    private fun isAccessGranted(context : Context) : Boolean = runCatching {
        val packageManager : PackageManager = context.packageManager
        val applicationInfo : ApplicationInfo = packageManager.getApplicationInfo(context.packageName , 0)
        val appOpsManager : AppOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        @Suppress("DEPRECATION") val mode : Int = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS , applicationInfo.uid , applicationInfo.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }.getOrElse { false }

    fun hasContactsPermissions(context: Context): Boolean {
        val readGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        val writeGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        return readGranted && writeGranted
    }

    fun requestContactsPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            ),
            AppPermissionsConstants.REQUEST_CODE_CONTACTS_PERMISSIONS
        )
    }
}