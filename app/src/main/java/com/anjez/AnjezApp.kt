package com.anjez

import android.app.Application
import com.anjez.data.local.TaskDatabase
import com.anjez.data.repository.TaskRepository
import com.anjez.utils.NotificationHelper

class AnjezApp : Application() {
    
    lateinit var repository: TaskRepository
    
    override fun onCreate() {
        super.onCreate()
        
        // تهيئة قاعدة البيانات
        val database = TaskDatabase.getDatabase(this)
        repository = TaskRepository(database.taskDao())
        
        // تهيئة قنوات التنبيهات
        NotificationHelper.createNotificationChannel(this)
    }
}
