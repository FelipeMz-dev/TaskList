package com.felipemz_dev.tasklist.ui.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.felipemz_dev.tasklist.data.local.TaskEntity
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.ui.recyclerView.viewholder.TaskViewHolder
import com.felipemz_dev.tasklist.ui.viewmodel.MainViewModel

class TaskAdapter(
    private val viewModel: MainViewModel,
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