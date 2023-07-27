package com.example.tasklist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskStepsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val tvStep: TextView = view.findViewById(R.id.tvStep)
    private val tvNumberStep: TextView = view.findViewById(R.id.tvNumberStep)

    fun render(step: String, position: Int){
        tvStep.text = step
        tvNumberStep.text = "$position."
    }
}