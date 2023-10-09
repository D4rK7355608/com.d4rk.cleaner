package com.d4rk.cleaner.dialogs
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import androidx.fragment.app.DialogFragment
import com.d4rk.cleaner.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
class RequireRestartDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.require_restart)
            .setMessage(R.string.summary_require_restart)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                activity?.recreate()
                val nManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nManager.cancelAll()
                val pm: PackageManager = requireContext().packageManager
                val intent = pm.getLaunchIntentForPackage(requireContext().packageName)
                intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                requireContext().startActivity(intent)
                Process.killProcess(Process.myPid())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
