package com.d4rk.cleaner.adapters
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ItemAppListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
class AppListAdapter(private val apps: List<ApplicationInfo>) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {
    private val scope = CoroutineScope(Dispatchers.IO)
    class ViewHolder(val binding: ItemAppListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: ApplicationInfo, context: Context) {
            binding.textViewAppName.text = app.loadLabel(context.packageManager)
            binding.textViewAppSize.text = formatSize(app.sourceDir)
            binding.buttonMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_app_manager, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_uninstall -> {
                            launchUninstall(app.packageName, context)
                            true
                        }
                        R.id.menu_share -> {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app")
                            val isFromPlayStore = isAppFromPlayStore(app.packageName, context.packageManager)
                            val appName = app.loadLabel(context.packageManager).toString()
                            val appPackage = app.packageName
                            if (isFromPlayStore) {
                                val playStoreLink = "https://play.google.com/store/apps/details?id=${app.packageName}"
                                val shareMessage = "Check out this app: $appName\n$playStoreLink"
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            } else {
                                val shareMessage = "Check out this app: $appName\n$appPackage"
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share App"))
                            true
                        }
                        R.id.menu_app_info -> {
                            val appInfoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val packageUri = Uri.fromParts("package", app.packageName, null)
                            appInfoIntent.data = packageUri
                            context.startActivity(appInfoIntent)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
        private fun launchUninstall(packageName: String, context: Context) {
            val uri = Uri.fromParts("package", packageName, null)
            val uninstallIntent = Intent(Intent.ACTION_DELETE, uri)
            context.startActivity(uninstallIntent)
        }
        private fun formatSize(path: String): String {
            val size = File(path).length()
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            var index = 0
            var sizeFloat = size.toFloat()
            while (sizeFloat > 1024 && index < units.size - 1) {
                sizeFloat /= 1024
                index++
            }
            return String.format("%.2f %s", sizeFloat, units[index])
        }
        private fun isAppFromPlayStore(packageName: String, packageManager: PackageManager): Boolean {
            val installSource = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val installSourceInfo = packageManager.getInstallSourceInfo(packageName)
                installSourceInfo.installingPackageName
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstallerPackageName(packageName)
            }
            return installSource == "com.android.vending"
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        val context = holder.itemView.context
        scope.launch {
            val drawable = app.loadIcon(context.packageManager)
            withContext(Dispatchers.Main) {
                Glide.with(context)
                    .load(drawable)
                    .into(holder.binding.imageViewAppIcon)
                holder.bind(app, context)
            }
        }
    }
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        scope.cancel()
    }
    override fun getItemCount(): Int {
        return apps.size
    }
}