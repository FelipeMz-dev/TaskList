package com.example.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TaskStepAdapter(private val steps: List<String>) : RecyclerView.Adapter<TaskStepsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskStepsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskStepsViewHolder(view)
    }

    override fun getItemCount() = steps.size

    override fun onBindViewHolder(holder: TaskStepsViewHolder, position: Int) {
        holder.render(steps[position], position)
    }

}