package com.example.tasklist.view.recyclerView.adapter

import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.data.entities.TaskEntity
import com.example.tasklist.R
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val tasks: List<TaskEntity>,
    private val listener: OnItemClickListener,
    private val checkedListener: OnCheckedChangeListener
) : RecyclerView.Adapter<TaskAdapter.TasksViewHolder>() {

    private var filteredItems: List<TaskEntity> = tasks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TasksViewHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(isChecked: Boolean, position: Int)
    }

    override fun getItemCount() = filteredItems.size

    fun filterTodo(){
        filteredItems = tasks.filter { it.done }
        notifyDataSetChanged()
    }

    fun filterDone(){
        filteredItems = tasks.filter { it.done.not() }
        notifyDataSetChanged()
    }

    fun filterAll(){
        filteredItems = tasks
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.render(filteredItems[position])
    }

    inner class TasksViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val taskText: TextView = view.findViewById(R.id.tvTask)
        private val taskCheckBox: CheckBox = view.findViewById(R.id.cbTask)
        private val taskDate: TextView = view.findViewById(R.id.tvDate)
        var currentDate: Date

        init {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            currentDate = dateFormat.parse(dateFormat.format(calendar.time))
        }

        fun render(taskEntity: TaskEntity) {

            itemView.setOnClickListener { listener.onItemClick(position) }
            taskCheckBox.setOnClickListener {
                checkedListener.onCheckedChange(taskCheckBox.isChecked, position)
            }
            taskCheckBox.isChecked = taskEntity.done
            taskText.text = taskEntity.taskText
            taskDate.text = taskEntity.expiryDate

            checkDateOfExpiry(tasks[tasks.indexOf(taskEntity)].expiryDate)

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

}