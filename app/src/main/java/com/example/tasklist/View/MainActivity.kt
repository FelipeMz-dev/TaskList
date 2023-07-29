package com.example.tasklist.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tasklist.DataBase.Entities.TaskEntity
import com.example.tasklist.R
import com.example.tasklist.RecyclerView.Adapter.TaskAdapter
import com.example.tasklist.DataBase.TaskDataBase
import com.example.tasklist.RecyclerView.Adapter.TaskAdapter.OnItemClickListener
import com.example.tasklist.RecyclerView.SwipeToDeleteCallback
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
        //loadDataTasks()
        initUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            loadDataTasks()
        }
    }

    private fun loadDataTasks() {
        Executors.newSingleThreadExecutor().execute {
            listTask.clear()
            listTask.addAll(dataBase.taskDao().getAllTasks())
        }

        taskAdapter.notifyDataSetChanged()
    }

    private fun deleteTasks(position: Int) {
        Executors.newSingleThreadExecutor().execute {
            dataBase.taskDao().deleteTask(listTask[position])
            listTask.removeAt(position)
        }
        // Actualizar un usuario
        //user1.email = "nuevo_email@example.com"
        //db.userDao().updateUser(user1)
    }

    private fun initComponents() {
        dataBase =
            Room.databaseBuilder(applicationContext, TaskDataBase::class.java, "task_db").build()
        rvTasks = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
    }

    private fun updateCheckedTask(isChecked: Boolean, task: TaskEntity) {
        Executors.newSingleThreadExecutor().execute {
            task.done = isChecked
            dataBase.taskDao().updateTask(task)
        }
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