package com.example.tasklist.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tasklist.data.entities.TaskEntity
import com.example.tasklist.R
import com.example.tasklist.view.recyclerView.adapter.TaskAdapter
import com.example.tasklist.data.dataBase.TaskDataBase
import com.example.tasklist.TaskRepository
import com.example.tasklist.TaskViewModel
import com.example.tasklist.TaskViewModelFactory
import com.example.tasklist.view.recyclerView.adapter.TaskAdapter.OnItemClickListener
import com.example.tasklist.view.recyclerView.SwipeToDeleteCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dataBase: TaskDataBase
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var btnFilter: Button
    private var listTask: MutableList<TaskEntity> = mutableListOf()
    private val REQUEST_CODE = 123
    private val filters = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initUI()
    }

    private fun initComponents() {
        dataBase = Room.databaseBuilder(
            applicationContext,
            TaskDataBase::class.java,
            "task_db"
        ).build()
        rvTasks = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        btnFilter = findViewById(R.id.btnFilter)
        filters.add(resources.getString(R.string.all))
        filters.add(resources.getString(R.string.done))
        filters.add(resources.getString(R.string.todo))
    }

    private fun initUI() {
        taskAdapter = TaskAdapter(listTask, object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(applicationContext, EditTaskActivity::class.java)
                intent.putExtra("id_task", listTask[position].id)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }, object : TaskAdapter.OnCheckedChangeListener {
            override fun onCheckedChange(isChecked: Boolean, position: Int) {
                //update when change the checkBox state
                updateCheckedTask(isChecked, listTask[position])
                changeTaskChecked()
            }
        })
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
        loadDataTasks()

        fabAddTask.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        btnFilter.text = filters[0]
        btnFilter.setOnClickListener {
            when (filters.indexOf(btnFilter.text)) {
                0 -> {
                    btnFilter.text = filters[1]
                    taskAdapter.filterTodo()
                }

                1 -> {
                    btnFilter.text = filters[2]
                    taskAdapter.filterDone()
                }

                else -> {
                    btnFilter.text = filters[0]
                    taskAdapter.filterAll()
                }
            }
        }

        val swipeToDeleteCallback = SwipeToDeleteCallback(taskAdapter) {
            deleteTasks(it)
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvTasks)
    }

    private fun changeTaskChecked() {
        when (btnFilter.text) {
            filters[1] -> {
                taskAdapter.filterTodo()
            }

            filters[2] -> {
                taskAdapter.filterDone()
            }
        }
    }

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            loadDataTasks()
        }
    }

    private fun loadDataTasks() {
        viewModel.getAllData().observe(this) { value ->
            listTask.clear()
            listTask.addAll(value)
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun deleteTasks(position: Int) {
        val dataToDelete = listTask[position]
        viewModel.deleteData(dataToDelete)
        listTask.removeAt(position)
    }

    private fun updateCheckedTask(isChecked: Boolean, task: TaskEntity) {
        task.done = isChecked
        viewModel.updateData(task)
    }
}