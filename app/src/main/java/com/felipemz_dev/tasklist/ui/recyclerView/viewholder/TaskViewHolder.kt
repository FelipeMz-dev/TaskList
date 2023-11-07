package com.felipemz_dev.tasklist.ui.recyclerView.viewholder

import android.content.res.ColorStateList
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
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
        taskDate.text = taskEntity.expiryDate

        checkDateOfExpiry(taskEntity.expiryDate)

    }

    private fun checkDateOfExpiry(strDate: String) {
        var colorExpiry = if (compareDates(strDate) >= 0)
            itemView.resources.getColor(R.color.wheat)
        else
            itemView.resources.getColor(R.color.white)

        taskText.setTextColor(colorExpiry)
        taskDate.setTextColor(colorExpiry)
        taskCheckBox.buttonTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_enabled)
            ),
            intArrayOf(colorExpiry)
        )
    }

    fun compareDates(dateStr: String): Int {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date2: Date = dateFormat.parse(dateStr)!!
        return currentDate.compareTo(date2)
    }

}