package com.d4rk.cleaner.clipboard
import android.app.ActivityManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.d4rk.cleaner.R
import java.text.NumberFormat
@Suppress("MemberVisibilityCanBePrivate")
class CleanService : Service(), ClipboardManager.OnPrimaryClipChangedListener {
    companion object {
        private const val PREF_SERVICE_STARTED = "pref_service_started"
        private const val PREF_SERVICE_OPTION = "pref_service_option"
        const val SERVICE_OPTION_CLEAN = 0
        const val SERVICE_OPTION_CONTENT = 1
        fun start(context: Context) {
            setServiceStarted(context, true)
            ActivityCompat.startForegroundService(
                context,
                Intent(context, CleanService::class.java)
            )
        }
        fun stop(context: Context) {
            setServiceStarted(context, false)
            context.stopService(Intent(context, CleanService::class.java))
        }
        fun getServiceStarted(context: Context) = context.getSafeSharedPreference()
            .getBoolean(PREF_SERVICE_STARTED, false)
        fun setServiceStarted(context: Context, started: Boolean) =
            context.getSafeSharedPreference()
                .edit().putBoolean(PREF_SERVICE_STARTED, started).apply()
        fun isServiceRunning(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            @Suppress("DEPRECATION")
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (CleanService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }
        fun getServiceOption(context: Context): Int = context.getSafeSharedPreference()
            .getInt(PREF_SERVICE_OPTION, SERVICE_OPTION_CLEAN)
        fun setServiceOption(context: Context, option: Int) {
            context.getSafeSharedPreference().edit()
                .putInt(PREF_SERVICE_OPTION, if (option in 0..1) option else 0).apply()
        }
    }
    private val cleanHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() {
        super.onCreate()
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .addPrimaryClipChangedListener(this)
        toast(R.string.clipboard_service_start)
    }
    override fun onPrimaryClipChanged() {
        if (currentContent().isEmpty()) return
        val option = getServiceOption(this)
        if (option == SERVICE_OPTION_CLEAN) {
            val timeout = serviceCleanTimeout
            if (timeout <= 0) {
                clean()
            } else {
                cleanHandler.removeCallbacksAndMessages(null)
                cleanHandler.postDelayed({
                    clean()
                }, timeout * 1_000L)
                toast(
                    getString(R.string.clipboard_service_start).format(
                        resources.getQuantityString(
                            R.plurals.seconds,
                            timeout,
                            NumberFormat.getInstance().format(timeout)
                        )
                    )
                )
            }
        } else if (option == SERVICE_OPTION_CONTENT) {
            content()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .removePrimaryClipChangedListener(this)
        toast(R.string.clipboard_service_stop)
    }
}