package com.example.app_mythology

import android.app.Application
import com.example.app_mythology.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MythosApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Peupler la base au démarrage si elle est vide
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.populateIfEmpty(applicationContext)
        }
    }
}
