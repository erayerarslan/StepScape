package com.erayerarslan.stepscape.ui.logs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erayerarslan.stepscape.data.local.entity.StepLog
import com.erayerarslan.stepscape.databinding.ItemStepLogBinding
import java.text.SimpleDateFormat
import java.util.*

class StepLogAdapter : ListAdapter<StepLog, StepLogAdapter.StepLogViewHolder>(StepLogDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepLogViewHolder {
        val binding = ItemStepLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepLogViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: StepLogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class StepLogViewHolder(
        private val binding: ItemStepLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(stepLog: StepLog) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = Date(stepLog.date)
            
            binding.tvStepLog.text = "StepLog: ${dateFormat.format(date)} - User 'Aurora' took ${stepLog.steps} steps. Data synced to Firebase."
        }
    }
    
    class StepLogDiffCallback : DiffUtil.ItemCallback<StepLog>() {
        override fun areItemsTheSame(oldItem: StepLog, newItem: StepLog): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: StepLog, newItem: StepLog): Boolean {
            return oldItem == newItem
        }
    }
}

