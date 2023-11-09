package com.felipemz_dev.tasklist.ui.recyclerView.viewholder

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.data.local.TaskEntity
import java.util.Date
import java.util.Locale

class TaskViewHolder (
    view: View,
    private val onTaskChecked: (task: TaskEntity) -> Unit,
): RecyclerView.ViewHolder(view) {

    private val taskText: TextView = view.findViewById(R.id.tvTask)
    private val taskCheckBox: CheckBox = view.findViewById(R.id.cbTask)
    private val taskDate: TextView = view.findViewById(R.id.tvDate)
    private var currentDate: Date
    private lateinit var task: TaskEntity
    fun getTask() = task

    init {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        currentDate = dateFormat.parse(dateFormat.format(calendar.time))
    }

    fun render(taskEntity: TaskEntity) {
        task = taskEntity
        taskCheckBox.setOnClickListener {
            val task = taskEntity.copy(done = taskCheckBox.isChecked)
            onTaskChecked(task)
        }
        taskCheckBox.isChecked = taskEntity.done
        taskText.text = taskEntity.taskText

        if (task.isRemember) checkDateOfExpiry()
        else taskDate.text = itemView.resources.getString(R.string.undefined)
    }

    private fun checkDateOfExpiry() {
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date: Date = format.parse(task.expiryDate)
        val textToDate = dateFormat.format(date) + " - " + timeFormat.format(date)
        taskDate.text = textToDate
        if (currentDate >= date) {
            taskDate.setTextColor(itemView.resources.getColor(R.color.wheat))
        } else {
            taskDate.setTextColor(itemView.resources.getColor(R.color.white))
            itemView.resources.getColor(R.color.white)
        }
    }
}