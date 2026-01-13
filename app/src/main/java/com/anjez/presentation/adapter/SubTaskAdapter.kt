package com.anjez.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anjez.data.local.SubTask
import com.anjez.databinding.ItemSubtaskBinding

class SubTaskAdapter(
    private val onSubTaskToggle: (SubTask) -> Unit,
    private val onSubTaskDelete: (SubTask) -> Unit
) : ListAdapter<SubTask, SubTaskAdapter.SubTaskViewHolder>(SubTaskDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val binding = ItemSubtaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubTaskViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class SubTaskViewHolder(private val binding: ItemSubtaskBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(subTask: SubTask) {
            binding.apply {
                checkboxSubTask.text = subTask.title
                checkboxSubTask.isChecked = subTask.isCompleted
                
                // Listener للتغيير
                checkboxSubTask.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != subTask.isCompleted) {
                        onSubTaskToggle(subTask)
                    }
                }
                
                // زر الحذف
                buttonDelete.setOnClickListener {
                    onSubTaskDelete(subTask)
                }
            }
        }
    }
    
    class SubTaskDiffCallback : DiffUtil.ItemCallback<SubTask>() {
        override fun areItemsTheSame(oldItem: SubTask, newItem: SubTask): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: SubTask, newItem: SubTask): Boolean {
            return oldItem == newItem
        }
    }
}
