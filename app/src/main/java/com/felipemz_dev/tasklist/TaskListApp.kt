package com.felipemz_dev.tasklist

import android.app.Application
import com.google.android.gms.ads.MobileAds

class TaskListApp: Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}