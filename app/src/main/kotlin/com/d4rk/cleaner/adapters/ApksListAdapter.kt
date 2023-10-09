package com.d4rk.cleaner.adapters
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.d4rk.cleaner.R
import com.d4rk.cleaner.databinding.ItemAppListBinding
import java.io.File
class ApksListAdapter(private val apkFiles: List<File>) : RecyclerView.Adapter<ApksListAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemAppListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(apkFile: File) {
            binding.imageViewAppIcon.setImageResource(R.mipmap.ic_launcher)
            binding.textViewAppName.text = apkFile.name
            binding.textViewAppSize.text = formatSize(apkFile.length())
            binding.buttonMenu.setOnClickListener { view ->
                val context = view.context
                val popupMenu = PopupMenu(context, view)
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
        fun bindIcon(icon: Drawable) {
            binding.imageViewAppIcon.setImageDrawable(icon)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val apkFile = apkFiles[position]
        holder.bind(apkFile)
        val context = holder.itemView.context
        val packageInfo = context.packageManager.getPackageArchiveInfo(apkFile.path, PackageManager.GET_META_DATA)
        packageInfo?.applicationInfo?.let {
            holder.bindIcon(it.loadIcon(context.packageManager))
        }
    }
    override fun getItemCount(): Int {
        return apkFiles.size
    }
}