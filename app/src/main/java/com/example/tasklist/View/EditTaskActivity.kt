package com.example.tasklist.View

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tasklist.DataBase.Entities.TaskEntity
import com.example.tasklist.R
import com.example.tasklist.DataBase.TaskDataBase
import com.example.tasklist.RecyclerView.Adapter.TaskStepAdapter
import com.example.tasklist.RecyclerView.SwipeToDeleteCallback
import java.util.Locale
import java.util.concurrent.Executors

class EditTaskActivity : AppCompatActivity() {

    private var taskModify: TaskEntity? = null
    private lateinit var dataBase: TaskDataBase
    private lateinit var etTaskTextEdit: EditText
    private lateinit var tvExpiryDateEdit: TextView
    private lateinit var btnAddTaskStep: ImageButton
    private lateinit var btnCancelTaskEdit: Button
    private lateinit var btnApplyTaskEdit: Button
    private lateinit var taskStepAdapter: TaskStepAdapter
    private lateinit var rvTaskSteps: RecyclerView
    private var listSteps = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        initComponents()
        initUI()
    }

    private fun getIdTaskExtra(): Boolean{
        var bundle :Bundle ?=intent.extras
        if (bundle != null) {
            val idTask = bundle.getLong("id_task")
            getTaskModify(idTask)
            return true
        }
        return false
    }

    private fun getTaskModify(id: Long){
        Executors.newSingleThreadExecutor().execute {
            val task = dataBase.taskDao().getTaskById(id)
            taskModify = task
            loadDataTask()
        }
    }

    private fun loadDataTask(){
        runOnUiThread {
            etTaskTextEdit.setText(taskModify!!.taskText)
            tvExpiryDateEdit.text = taskModify!!.expiryDate
            val stepsString = taskModify!!.listSteps
            if (!stepsString.isEmpty()) {
                listSteps.clear()
                listSteps.addAll(stepsString.split ("|").toList())
            }
            taskStepAdapter.notifyDataSetChanged()
        }

    }

    private fun initComponents(){
        dataBase = Room.databaseBuilder(applicationContext, TaskDataBase::class.java, "task_db").build()
        etTaskTextEdit = findViewById(R.id.etTaskTextEdit)
        tvExpiryDateEdit = findViewById(R.id.tvExpiryDateEdit)
        btnAddTaskStep = findViewById(R.id.btnAddTaskStep)
        btnApplyTaskEdit = findViewById(R.id.btnApplyTaskEdit)
        btnCancelTaskEdit = findViewById(R.id.btnCancelTaskEdit)
        rvTaskSteps = findViewById(R.id.rvTaskSteps)
    }

    private fun initUI(){
        tvExpiryDateEdit.setOnClickListener { showDatePicker() }
        btnAddTaskStep.setOnClickListener { showInputDialog() }
        btnCancelTaskEdit.setOnClickListener { finish() }
        btnApplyTaskEdit.setOnClickListener {
            if (taskModify != null) updateTask()
            else saveNewTask()
        }
        //List of steps
        //listSteps.add("comenzar por investigar")
        taskStepAdapter = TaskStepAdapter(listSteps)
        rvTaskSteps.layoutManager = LinearLayoutManager(this)
        rvTaskSteps.adapter = taskStepAdapter

        val swipeToDeleteCallback = SwipeToDeleteCallback(taskStepAdapter) {
            listSteps.removeAt(it)
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvTaskSteps)
        if (!getIdTaskExtra()) updateDateOnTextView(Calendar.getInstance()) //load task or Current Date
    }

    private fun updateTask(){
        val taskText = etTaskTextEdit.text.toString()
        if (taskText.isEmpty()) return
        val expiryDate = tvExpiryDateEdit.text.toString()
        taskModify!!.taskText = taskText
        taskModify!!.expiryDate = expiryDate
        taskModify!!.listSteps = listSteps.joinToString("|")
        Executors.newSingleThreadExecutor().execute {
            dataBase.taskDao().updateTask(taskModify!!)
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun saveNewTask(){
        val taskText = etTaskTextEdit.text.toString()
        if (taskText.isEmpty()) return
        val expiryDate = tvExpiryDateEdit.text.toString()
        val task = TaskEntity(0,taskText, expiryDate, listSteps.joinToString("|"))
        Executors.newSingleThreadExecutor().execute {
            dataBase.taskDao().insertTask(task)
            setResult(RESULT_OK)
            finish()
        }
        Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show()
    }

    private fun showInputDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_task_step, null)
        dialogBuilder.setView(dialogView)

        val inputEditText: EditText = dialogView.findViewById(R.id.inputEditText)

        dialogBuilder.setTitle(R.string.new_step)
        dialogBuilder.setPositiveButton(R.string.add) { _, _ ->
            val userInputText = inputEditText.text.toString()
            addStep(userInputText)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun addStep(text: String){
        listSteps.add(text)
        taskStepAdapter.notifyItemInserted(listSteps.size)
    }

    private fun showDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, yearSelected, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(yearSelected, monthOfYear, dayOfMonth)
            updateDateOnTextView(selectedDate)
        }, year, month, day)

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun updateDateOnTextView(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        tvExpiryDateEdit.text = formattedDate
    }
}