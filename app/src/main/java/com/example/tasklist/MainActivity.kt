package com.example.tasklist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.actions.FloatAction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private var listTask = mutableListOf<TaskElement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initUI()
    }

    private fun initComponents(){
        rvTasks = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
    }

    private fun initUI(){
        listTask.add(TaskElement("Hacer mi tarea", "10/02/2023", false))
        taskAdapter = TaskAdapter(listTask)
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter

        fabAddTask.setOnClickListener{
            val intent = Intent(this, EditTaskActivity::class.java)
            startActivity(intent)
        }
    }
}