package com.example.tasklist.RecyclerView.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R

class TaskStepAdapter(private val steps: List<String>) : RecyclerView.Adapter<TaskStepAdapter.TaskStepsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskStepsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_step_to_task, parent, false)
        return TaskStepsViewHolder(view)
    }

    override fun getItemCount() = steps.size

    override fun onBindViewHolder(holder: TaskStepsViewHolder, position: Int) {
        holder.render(steps[position], position+1)
    }

    inner class TaskStepsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val tvStep: TextView = view.findViewById(R.id.tvStep)
        private val tvNumberStep: TextView = view.findViewById(R.id.tvNumberStep)

        fun render(step: String, position: Int){
            tvStep.text = step
            tvNumberStep.text = "$position."
        }
    }

}