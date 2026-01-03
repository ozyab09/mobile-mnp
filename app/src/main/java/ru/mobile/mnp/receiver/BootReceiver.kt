package ru.mobile.mnp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.mobile.mnp.service.CallDetectionService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            // Start the call detection service after boot
            val serviceIntent = Intent(context, CallDetectionService::class.java)
            context.startService(serviceIntent)
        }
    }
}