package com.felipemz_dev.tasklist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.felipemz_dev.tasklist.data.local.dao.TaskDao
import com.felipemz_dev.tasklist.data.local.TaskDataBase
import com.felipemz_dev.tasklist.data.local.TaskEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataDaoTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TaskDataBase
    private lateinit var dataDao: TaskDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, TaskDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        dataDao = database.taskDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertDataTest() = runBlocking {
        val data = TaskEntity(id = 1,
            taskText = "do the homework",
            expiryDate = "31/07/2023",
            listSteps = "complete the first step",
            isRemember = true,
            done = false
        )
        dataDao.insertTask(data)

        val insertedData = dataDao.getTaskById(1)
        assert(insertedData != null)
        assert(insertedData?.taskText == "do the homework")
    }
}