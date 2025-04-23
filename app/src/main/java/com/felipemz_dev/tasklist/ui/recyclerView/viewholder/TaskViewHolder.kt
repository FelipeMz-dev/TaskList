package com.felipemz_dev.tasklist.ui.recyclerView.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.luminance
import androidx.recyclerview.widget.RecyclerView
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.utils.TextDateUtils
import com.felipemz_dev.tasklist.data.local.TaskEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskViewHolder(
    view: View,
    private val onTaskChecked: (task: TaskEntity) -> Unit,
) : RecyclerView.ViewHolder(view) {

    private val card: CardView = view.findViewById(R.id.cardItem)
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
        val color = when (task.color) {
            1 -> itemView.resources.getColor(R.color.red)
            2 -> itemView.resources.getColor(R.color.green)
            3 -> itemView.resources.getColor(R.color.blue)
            4 -> itemView.resources.getColor(R.color.yellow)
            5 -> itemView.resources.getColor(R.color.purple)
            6 -> itemView.resources.getColor(R.color.orange)
            7 -> itemView.resources.getColor(R.color.pink)
            8 -> itemView.resources.getColor(R.color.cyan)
            else -> itemView.resources.getColor(R.color.surfie_green)
        }

        taskCheckBox.setOnClickListener { onTaskChecked(task) }
        taskCheckBox.isChecked = taskEntity.done
        taskText.text = taskEntity.taskText
        card.setCardBackgroundColor(color)
        val colorText = if (ColorUtils.calculateLuminance(color) < 0.5) Color.WHITE else Color.BLACK
        val todayColor = if (ColorUtils.calculateLuminance(color) < 0.5) itemView.resources.getColor(R.color.wheat)
        else itemView.resources.getColor(R.color.apricot)
        taskText.setTextColor(colorText)
        taskCheckBox.buttonTintList = ColorStateList.valueOf(colorText)
        taskDate.setTextColor(colorText)
        taskDate.compoundDrawableTintList = ColorStateList.valueOf(colorText)
        tvToday.setTextColor(todayColor)
        tvToday.compoundDrawableTintList = ColorStateList.valueOf(todayColor)

        if (task.isRemember && task.expiryDate.trim().isNotEmpty()) {
            setNotificationOn(true)
            checkDateOfExpiry()
        } else {
            taskDate.text = itemView.resources.getString(R.string.undefined)
            setNotificationOn(false)
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
            if (TextDateUtils.isDateExpired(date.time) || task.done) {
                setNotificationOn(false)
            }
        }
    }

    private fun setNotificationOn(isOn: Boolean) {
        if (isOn) {
            taskDate.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                itemView.resources.getDrawable(R.drawable.round_notifications_24),
                null
            )
        } else {
            taskDate.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                itemView.resources.getDrawable(R.drawable.outline_notifications_off_24),
                null
            )
        }
    }
}