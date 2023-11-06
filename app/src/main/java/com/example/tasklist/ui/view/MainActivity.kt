package com.example.tasklist.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.tasklist.R
import com.example.tasklist.ui.recyclerView.adapter.TaskAdapter
import com.example.tasklist.data.local.TaskDataBase
import com.example.tasklist.data.TaskRepository
import com.example.tasklist.ui.viewmodel.TaskViewModel
import com.example.tasklist.core.TaskViewModelFactory
import com.example.tasklist.ui.getCircularIndex
import com.example.tasklist.ui.recyclerView.viewholder.TaskViewHolder
import com.example.tasklist.ui.onSwipeItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var btnFilter: Button
    private lateinit var filters: Array<String>

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initUI()
    }

    private fun initComponents() {
        rvTasks = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        btnFilter = findViewById(R.id.btnFilter)
        filters = resources.getStringArray(R.array.filters)
    }

    private fun initUI() {
        taskAdapter = TaskAdapter(viewModel) { goEditTaskActivity(it) }
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
        rvTasks.onSwipeItem { deleteItem(it) }
        btnFilter.text = filters[0]
        btnFilter.setOnClickListener { onChangeFilter(it) }
        fabAddTask.setOnClickListener { goEditTaskActivity() }
    }

    private fun deleteItem(viewHolder: ViewHolder) {
        viewHolder as TaskViewHolder
        viewModel.deleteData(viewHolder.getTask())
    }

    private fun goEditTaskActivity(id: Long? = null) {
        val intent = Intent(this, EditTaskActivity::class.java)
        if (id != null) intent.putExtra("id_task", id)
        startActivity(intent)
    }

    private fun onChangeFilter(view: View) {
        view as Button
        val index = filters.indexOf(view.text)
        view.text = filters[filters.getCircularIndex(index)]
        when (index) {
            0 -> viewModel.filterTodo()
            1 -> viewModel.filterDone()
            else -> viewModel.filterAllData()
        }
    }
}