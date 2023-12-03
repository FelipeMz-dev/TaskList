package com.felipemz_dev.tasklist.ui.recyclerView.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.data.local.TaskEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskViewHolder(
    view: View,
    private val onTaskChecked: (task: TaskEntity) -> Unit,
) : RecyclerView.ViewHolder(view) {

    private val taskText: TextView = view.findViewById(R.id.tvTask)
    private val taskCheckBox: CheckBox = view.findViewById(R.id.cbTask)
    private val taskDate: TextView = view.findViewById(R.id.tvDate)
    private val tvToday: TextView = view.findViewById(R.id.tvToday)
    private var currentDate: Date
    private lateinit var task: TaskEntity
    fun getTask() = task

    init {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        currentDate = dateFormat.parse(dateFormat.format(calendar.time)) as Date
    }

    fun render(taskEntity: TaskEntity) {
        tvToday.visibility = View.GONE
        tvToday.text = itemView.resources.getString(R.string.str_today)
        tvToday.setCompoundDrawablesWithIntrinsicBounds(
            itemView.resources.getDrawable(R.drawable.round_warning_24),
            null, null, null
        )
        task = taskEntity
        taskCheckBox.setOnClickListener { onTaskChecked(task) }
        taskCheckBox.isChecked = taskEntity.done
        taskText.text = taskEntity.taskText

        if (task.isRemember && task.expiryDate.trim().isNotEmpty()) {
            checkDateOfExpiry()
            taskDate.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                itemView.resources.getDrawable(R.drawable.round_notifications_24),
                null
            )
        }
        else {
            taskDate.apply {
                text = itemView.resources.getString(R.string.undefined)
                setCompoundDrawablesWithIntrinsicBounds(
                    null, null,
                    itemView.resources.getDrawable(R.drawable.outline_notifications_off_24),
                    null
                )
            }
        }
    }

    private fun checkDateOfExpiry() {
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date: Date? = format.parse(task.expiryDate)
        val textToDate = date?.let {
            dateFormat.format(it)
        } + " - " + date?.let {
            timeFormat.format(it)
        }
        taskDate.text = textToDate
        if (date != null) {
            tvToday.visibility = if (currentDate.day == date.day) View.VISIBLE else View.GONE
            if (currentDate.time > date.time) tvToday.apply {
                visibility = View.VISIBLE
                text = itemView.resources.getString(R.string.str_expired)
                setCompoundDrawablesWithIntrinsicBounds(
                    itemView.resources.getDrawable(R.drawable.baseline_report_gmailerrorred_24),
                    null, null, null
                )
            }
        }
    }
}