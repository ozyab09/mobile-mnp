package com.example.mnpdetector

import android.app.Application
import android.content.Intent
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.mnpdetector.service.CallDetectionService
import com.example.mnpdetector.workmanager.WorkManagerHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MNPApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workManagerHelper: WorkManagerHelper
    
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        
        // Start the call detection service
        val intent = Intent(this, CallDetectionService::class.java)
        startService(intent)
        
        // Schedule periodic updates
        CoroutineScope(Dispatchers.IO).launch {
            workManagerHelper.schedulePeriodicUpdate()
        }
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}