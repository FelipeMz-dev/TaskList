package com.example.tasklist

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksViewHolder(view:View): RecyclerView.ViewHolder(view) {

    private val taskText:TextView = view.findViewById(R.id.tvTask)
    private val taskCheckBox:CheckBox = view.findViewById(R.id.cbTask)

    fun render(taskElement: TaskElement){
        taskText.text = taskElement.taskText
    }
}