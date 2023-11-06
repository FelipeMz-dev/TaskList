package com.example.tasklist.ui.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.data.local.TaskEntity
import com.example.tasklist.R
import com.example.tasklist.ui.recyclerView.viewholder.TaskViewHolder
import com.example.tasklist.ui.viewmodel.TaskViewModel

class TaskAdapter(
    private val viewModel: TaskViewModel,
    private val onItemClickListener: (Long) -> Unit,
) : RecyclerView.Adapter<TaskViewHolder>() {

    private var taskList: List<TaskEntity> = emptyList()

    init {
        viewModel.filterAllData()
        viewModel.filteredTask.observeForever {
            taskList = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = taskList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view){ viewModel.updateData(it) }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = taskList[position]
        holder.render(item)
        holder.itemView.setOnClickListener {
            onItemClickListener(item.id)
        }
    }

}