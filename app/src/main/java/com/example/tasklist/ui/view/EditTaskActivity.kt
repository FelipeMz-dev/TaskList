package com.example.tasklist.ui.view

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.data.local.TaskEntity
import com.example.tasklist.R
import com.example.tasklist.data.local.TaskDataBase
import com.example.tasklist.data.TaskRepository
import com.example.tasklist.ui.viewmodel.TaskViewModel
import com.example.tasklist.core.TaskViewModelFactory
import com.example.tasklist.ui.recyclerView.adapter.TaskStepAdapter
import com.example.tasklist.ui.onSwipeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private var taskModify: TaskEntity? = null
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

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    private fun getIdTaskExtra(): Boolean {
        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            val idTask = bundle.getLong("id_task")
            getTaskModify(idTask)
            return true
        }
        return false
    }

    private fun getTaskModify(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            taskModify = viewModel.getItemData(id)
            loadDataTask()
        }

    }

    private fun loadDataTask() {
        runOnUiThread {
            etTaskTextEdit.setText(taskModify!!.taskText)
            tvExpiryDateEdit.text = taskModify!!.expiryDate
            val stepsString = taskModify!!.listSteps
            if (stepsString.isNotEmpty()) {
                listSteps.clear()
                listSteps.addAll(stepsString.split("|").toList())
            }
            taskStepAdapter.notifyDataSetChanged()
        }

    }

    private fun initComponents() {
        etTaskTextEdit = findViewById(R.id.etTaskTextEdit)
        tvExpiryDateEdit = findViewById(R.id.tvExpiryDateEdit)
        btnAddTaskStep = findViewById(R.id.btnAddTaskStep)
        btnApplyTaskEdit = findViewById(R.id.btnApplyTaskEdit)
        btnCancelTaskEdit = findViewById(R.id.btnCancelTaskEdit)
        rvTaskSteps = findViewById(R.id.rvTaskSteps)
    }

    private fun initUI() {
        tvExpiryDateEdit.setOnClickListener { showDatePicker() }
        btnAddTaskStep.setOnClickListener { showInputDialog() }
        btnCancelTaskEdit.setOnClickListener { finish() }
        btnApplyTaskEdit.setOnClickListener {
            if (taskModify != null) updateTask()
            else saveNewTask()
        }
        //List of steps
        //listSteps.add("comenzar por investigar")
        taskStepAdapter = TaskStepAdapter(listSteps) { position: Int, text: String ->
            showInputDialog(position, text)
        }
        rvTaskSteps.layoutManager = LinearLayoutManager(this)
        rvTaskSteps.adapter = taskStepAdapter
        rvTaskSteps.onSwipeItem {
            val position = it.adapterPosition
            taskStepAdapter.deleteItem(position)
        }

        if (!getIdTaskExtra()) updateDateOnTextView(Calendar.getInstance()) //load task or Current Date
    }

    private fun updateTask() {
        val taskText = etTaskTextEdit.text.toString()
        if (taskText.isEmpty()) return
        val expiryDate = tvExpiryDateEdit.text.toString()
        taskModify!!.taskText = taskText
        taskModify!!.expiryDate = expiryDate
        taskModify!!.listSteps = listSteps.joinToString("|")
        viewModel.updateData(taskModify!!)
        finish()
    }

    private fun saveNewTask() {
        val taskText = etTaskTextEdit.text.toString()
        if (taskText.isEmpty()) return
        val expiryDate = tvExpiryDateEdit.text.toString()
        val task = TaskEntity(0, taskText, expiryDate, listSteps.joinToString("|"))
        viewModel.insertData(task)
        finish()
    }

    private fun showInputDialog() {
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

    private fun showInputDialog(position: Int, text: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_task_step, null)
        dialogBuilder.setView(dialogView)

        val inputEditText: EditText = dialogView.findViewById(R.id.inputEditText)
        inputEditText.setText(text)
        inputEditText.requestFocus()

        dialogBuilder.setTitle(R.string.new_step)
        dialogBuilder.setPositiveButton(R.string.update) { _, _ ->
            val userInputText = inputEditText.text.toString()
            editStep(userInputText, position)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun addStep(text: String) {
        if (text.isNotEmpty()) {
            listSteps.add(text)
            taskStepAdapter.notifyItemInserted(listSteps.size)
        }
    }

    private fun editStep(text: String, position: Int) {
        if (text.isNotEmpty()) {
            listSteps[position - 1] = text
            taskStepAdapter.notifyItemChanged(position - 1)
        }
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