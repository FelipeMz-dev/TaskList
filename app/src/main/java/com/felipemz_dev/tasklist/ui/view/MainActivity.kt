package com.felipemz_dev.tasklist.ui.view

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.ConsentManager
import com.felipemz_dev.tasklist.core.notifications.NotificationScheduler
import com.felipemz_dev.tasklist.core.FinalSpaceItemDecoration
import com.felipemz_dev.tasklist.ui.recyclerView.adapter.TaskAdapter
import com.felipemz_dev.tasklist.data.local.TaskDataBase
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.ui.viewmodel.MainViewModel
import com.felipemz_dev.tasklist.core.viewmodelfactory.MainViewModelFactory
import com.felipemz_dev.tasklist.core.extensions.getCircularIndex
import com.felipemz_dev.tasklist.core.extensions.makeResourcesToast
import com.felipemz_dev.tasklist.ui.recyclerView.viewholder.TaskViewHolder
import com.felipemz_dev.tasklist.core.extensions.onSwipeItem
import com.felipemz_dev.tasklist.core.extensions.showDialogDelete
import com.felipemz_dev.tasklist.core.extensions.showDialogDoneTask
import com.felipemz_dev.tasklist.data.local.TaskEntity
import com.felipemz_dev.tasklist.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG_GET_TASK = "TAG_DONE_TASK"
        const val ACTION_CHECK_DONE = "CHECK_DONE"
        const val ACTION_EDIT_TASK = "EDIT_TASK"
        const val ACTION_DELETE_TASK = "DELETE_TASK"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var filters: Array<String>
    private lateinit var alarmHelper: NotificationScheduler
    private lateinit var consentManager: ConsentManager

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        consentManager = ConsentManager(this)
        filters = resources.getStringArray(R.array.filters)
        alarmHelper = NotificationScheduler(this)
        viewModel.setAlarmHelper(alarmHelper)
        initUI()
        initAds()
        viewModel.isLoading.observe(this) {
            if (!it) initExtraData()
        }
    }

    private fun initExtraData() {
        val bundle = intent.extras
        if (bundle != null) {
            val id = bundle.getLong(TAG_GET_TASK, -1)
            if (id != -1L) {
                val task = viewModel.getItemData(id)
                if (task != null) {
                    when (intent.action) {
                        ACTION_CHECK_DONE -> {
                            updateItem(task)
                        }
                        ACTION_DELETE_TASK -> {
                            deleteItem(task)
                        }
                        ACTION_EDIT_TASK -> {
                            goEditTaskActivity(id)
                        }
                    }
                }
            }
        }
    }

    private fun initUI() {
        val context = this
        taskAdapter = TaskAdapter(viewModel, {
            goEditTaskActivity(it)
        }) { updateItem(it) }
        binding.apply {
            btnFilter.setOnClickListener { onChangeFilter() }
            fabAddTask.setOnClickListener { goEditTaskActivity() }
            rvTasks.addItemDecoration(FinalSpaceItemDecoration(150))
            rvTasks.layoutManager = LinearLayoutManager(context)
            rvTasks.adapter = taskAdapter
            rvTasks.onSwipeItem { deleteItem((it as TaskViewHolder).getTask()) }
            btnFilter.text = filters[0]
            viewModel.isLoading.observe(context) {
                pbLoading.visibility = if (it) View.VISIBLE else View.GONE
            }
            viewModel.isEmpty.observe(context) {
                ivEmptyList.visibility = if (it) View.VISIBLE else View.GONE
                tvAddTaskIndication.visibility = if (it) View.VISIBLE else View.GONE
                tvFilterHereIndication.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }

    private fun deleteItem(task: TaskEntity) {
        showDialogDelete(task.taskText) {
            if (it) viewModel.deleteData(task)
            else onReloadFilter()
        }
    }

    private fun updateItem(item: TaskEntity) {
        if (item.done) {
            viewModel.updateData(item)
        } else showDialogDoneTask(item.taskText) {
            if (it) viewModel.updateData(item)
            else onReloadFilter()
        }
    }

    private fun goEditTaskActivity(id: Long? = null) {
        val intent = Intent(this, EditTaskActivity::class.java)
        if (id != null) intent.putExtra("id_task", id)
        startActivity(intent)
    }

    private fun onChangeFilter() {
        val index = filters.indexOf(binding.btnFilter.text)
        binding.btnFilter.text = filters[filters.getCircularIndex(index)]
        when (index) {
            0 -> viewModel.filterTodo()
            1 -> viewModel.filterDone()
            else -> viewModel.filterAllData()
        }
    }

    private fun onReloadFilter() {
        when (filters.indexOf(binding.btnFilter.text)) {
            1 -> viewModel.filterTodo()
            2 -> viewModel.filterDone()
            else -> viewModel.filterAllData()
        }
    }

    //                                                  <-ADS
    override fun onResume() {
        super.onResume()
        consentManager.checkConsentAndLoadAds { loadAds() }
    }

    private fun loadAds() {
        initAds()
        binding.adBanner.loadAd(AdRequest.Builder().build())
    }

    private fun initAds() {
        binding.adBanner.adListener = object : AdListener() {
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
}