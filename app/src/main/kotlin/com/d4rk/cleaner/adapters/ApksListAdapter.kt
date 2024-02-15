package com.d4rk.cleaner.adapters
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
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

class ApksListAdapter(private val apkFiles: List<File>) : RecyclerView.Adapter<ApksListAdapter.ViewHolder>() {
    private val scope = CoroutineScope(Dispatchers.IO)

    class ViewHolder(val binding: ItemAppListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(apkFile: File, context: Context) {
            binding.imageViewAppIcon.setImageResource(R.mipmap.ic_launcher)
            binding.textViewAppName.text = apkFile.name
            binding.textViewAppSize.text = formatSize(apkFile.length())
            binding.buttonMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_app_manager, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_share -> {
                            val apkFileUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", apkFile)
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "application/vnd.android.package-archive"
                            shareIntent.putExtra(Intent.EXTRA_STREAM, apkFileUri)
                            context.startActivity(Intent.createChooser(shareIntent, "Share APK"))
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.menu.removeItem(R.id.menu_uninstall)
                popupMenu.menu.removeItem(R.id.menu_app_info)
                popupMenu.show()
            }
        }
        private fun formatSize(size: Long): String {
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            var index = 0
            var sizeFloat = size.toFloat()
            while (sizeFloat > 1024 && index < units.size - 1) {
                sizeFloat /= 1024
                index++
            }
            return String.format("%.2f %s", sizeFloat, units[index])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val apkFile = apkFiles[position]
        val context = holder.itemView.context
        holder.bind(apkFile, context)
        scope.launch {
            val packageInfo = context.packageManager.getPackageArchiveInfo(apkFile.path, PackageManager.GET_META_DATA)
            packageInfo?.applicationInfo?.let {
                val drawable = it.loadIcon(context.packageManager)
                withContext(Dispatchers.Main) {
                    Glide.with(context)
                        .load(drawable)
                        .into(holder.binding.imageViewAppIcon)
                }
            }
        }
    }
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        scope.cancel()
    }

    override fun getItemCount(): Int {
        return apkFiles.size
    }
}
