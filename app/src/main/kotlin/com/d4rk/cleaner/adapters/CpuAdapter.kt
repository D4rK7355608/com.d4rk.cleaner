package com.d4rk.cleaner.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.d4rk.cleaner.data.CpuApp
import com.d4rk.cleaner.data.CpuAppDiffCallback
import com.d4rk.cleaner.databinding.ItemCpuListBinding
class CpuAdapter(private var apps: List<CpuApp>) : RecyclerView.Adapter<CpuAdapter.CpuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CpuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCpuListBinding.inflate(inflater, parent, false)
        return CpuViewHolder(binding)
    }
    override fun onBindViewHolder(holder: CpuViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app)
    }
    override fun getItemCount(): Int {
        return apps.size
    }
    fun updateAppsList(newApps: List<CpuApp>) {
        val diffCallback = CpuAppDiffCallback(apps, newApps)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        apps = newApps
        diffResult.dispatchUpdatesTo(this)
    }

    inner class CpuViewHolder(private val binding: ItemCpuListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: CpuApp) {
            binding.appIcon.setImageDrawable(app.icon)
        }
    }
}