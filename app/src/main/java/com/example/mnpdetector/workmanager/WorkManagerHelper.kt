package com.example.mnpdetector.workmanager

import android.content.Context
import androidx.work.*
import com.example.mnpdetector.worker.DatabaseUpdateWorker
import com.example.mnpdetector.util.PreferencesHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesHelper: PreferencesHelper
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    fun schedulePeriodicUpdate() {
        if (!preferencesHelper.isAutoUpdateEnabled()) {
            // Cancel any existing work
            workManager.cancelUniqueWork(PERIODIC_UPDATE_WORK_NAME)
            return
        }
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Need internet connection
        
        if (preferencesHelper.isWifiOnly()) {
            constraints.setRequiredNetworkType(NetworkType.UNMETERED) // Wi-Fi only
        }
        
        if (preferencesHelper.isChargingOnly()) {
            constraints.setRequiresCharging(true) // Only when charging
        }
        
        val constraintsBuilder = constraints.build()
        
        val repeatInterval = when (preferencesHelper.getUpdateFrequency()) {
            "Weekly" -> 7L
            "Bi-weekly" -> 14L
            "Monthly" -> 30L
            else -> 7L // Default to weekly
        }
        
        val periodicWork = PeriodicWorkRequestBuilder<DatabaseUpdateWorker>(
            repeatInterval, TimeUnit.DAYS
        )
        .setConstraints(constraintsBuilder)
        .build()
        
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_UPDATE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
            periodicWork
        )
    }
    
    fun triggerManualUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val oneTimeWork = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
            .setConstraints(constraints)
            .build()
        
        workManager.enqueueUniqueWork(
            MANUAL_UPDATE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWork
        )
    }
    
    companion object {
        private const val PERIODIC_UPDATE_WORK_NAME = "periodic_mnp_update"
        private const val MANUAL_UPDATE_WORK_NAME = "manual_mnp_update"
    }
}