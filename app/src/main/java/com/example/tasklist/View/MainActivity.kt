package com.example.tasklist.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import androidx.room.Room
import com.example.tasklist.Data.DataBase.Dao.TaskDao
import com.example.tasklist.Data.Entities.TaskEntity
import com.example.tasklist.R
import com.example.tasklist.View.RecyclerView.Adapter.TaskAdapter
import com.example.tasklist.Data.DataBase.TaskDataBase
import com.example.tasklist.TaskRepository
import com.example.tasklist.TaskViewModel
import com.example.tasklist.TaskViewModelFactory
import com.example.tasklist.View.RecyclerView.Adapter.TaskAdapter.OnItemClickListener
import com.example.tasklist.View.RecyclerView.SwipeToDeleteCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var dataBase: TaskDataBase
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private var listTask: MutableList<TaskEntity> = mutableListOf()
    private val REQUEST_CODE = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initUI()
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
        viewModel.getAllData().observe(this, Observer{ value ->
            listTask.clear()
            listTask.addAll(value)
            taskAdapter.notifyDataSetChanged()
        })
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

    private fun initComponents() {
        dataBase =
            Room.databaseBuilder(applicationContext, TaskDataBase::class.java, "task_db").build()
        rvTasks = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
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
            }
        })
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
        loadDataTasks()

        fabAddTask.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        val swipeToDeleteCallback = SwipeToDeleteCallback(taskAdapter) {
            deleteTasks(it)
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvTasks)
    }
}