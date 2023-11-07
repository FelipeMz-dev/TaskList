package com.felipemz_dev.tasklist.ui.recyclerView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.felipemz_dev.tasklist.R

class TaskStepAdapter(
    private var steps: MutableList<String>,
    private val onItemClickListener: (Int, String) -> Unit
) : RecyclerView.Adapter<TaskStepAdapter.TaskStepsViewHolder>() {

    override fun getItemCount() = steps.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskStepsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_step_to_task, parent, false)
        return TaskStepsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskStepsViewHolder, position: Int) {
        holder.render(steps[position], position+1)
    }

    fun deleteItem(position: Int){
        steps.removeAt(position)
        notifyDataSetChanged()
    }

    inner class TaskStepsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val tvStep: TextView = view.findViewById(R.id.tvStep)
        private val tvNumberStep: TextView = view.findViewById(R.id.tvNumberStep)

        fun render(step: String, position: Int){
            tvStep.text = step
            tvNumberStep.text = "$position."
            itemView.setOnClickListener { onItemClickListener(position, step) }
        }
    }

}