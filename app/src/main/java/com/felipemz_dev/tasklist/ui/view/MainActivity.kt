package com.felipemz_dev.tasklist.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.AlarmHelper
import com.felipemz_dev.tasklist.ui.recyclerView.adapter.TaskAdapter
import com.felipemz_dev.tasklist.data.local.TaskDataBase
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.ui.viewmodel.MainViewModel
import com.felipemz_dev.tasklist.core.MainViewModelFactory
import com.felipemz_dev.tasklist.core.getCircularIndex
import com.felipemz_dev.tasklist.ui.recyclerView.viewholder.TaskViewHolder
import com.felipemz_dev.tasklist.core.onSwipeItem
import com.felipemz_dev.tasklist.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var filters: Array<String>
    private lateinit var alarmHelper: AlarmHelper

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filters = resources.getStringArray(R.array.filters)
        alarmHelper = AlarmHelper(this)
        viewModel.setAlarmHelper(alarmHelper)
        initUI()
        initAds()
    }

    private fun initAds() {
        binding.adView.loadAd(AdRequest.Builder().build())
        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                binding.adView.visibility = View.VISIBLE
            }

            override fun onAdClosed() {
                super.onAdClosed()
                binding.adView.visibility = View.GONE
                initAds()
            }
        }
    }

    private fun initUI() {
        taskAdapter = TaskAdapter(viewModel) { goEditTaskActivity(it) }
        binding.btnFilter.setOnClickListener { onChangeFilter(it as Button ) }
        binding.fabAddTask.setOnClickListener { goEditTaskActivity() }
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = taskAdapter
        binding.rvTasks.onSwipeItem { deleteItem(it as TaskViewHolder) }
        binding.btnFilter.text = filters[0]
        viewModel.isLoading.observe(this) {
            binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.isEmpty.observe(this) {
            binding.ivEmptyList.visibility = if (it) View.VISIBLE else View.GONE
            binding.tvAddTaskIndication.visibility = if (it) View.VISIBLE else View.GONE
            binding.tvFilterHereIndication.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun deleteItem(viewHolder: TaskViewHolder) {
        val task = viewHolder.getTask()
        viewModel.deleteData(task)
    }

    private fun goEditTaskActivity(id: Long? = null) {
        val intent = Intent(this, EditTaskActivity::class.java)
        if (id != null) intent.putExtra("id_task", id)
        startActivity(intent)
    }

    private fun onChangeFilter(view: Button) {
        val index = filters.indexOf(view.text)
        view.text = filters[filters.getCircularIndex(index)]
        when (index) {
            0 -> viewModel.filterTodo()
            1 -> viewModel.filterDone()
            else -> viewModel.filterAllData()
        }
    }
}