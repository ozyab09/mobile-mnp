package ru.mobile.mnp.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ServiceCompat
import ru.mobile.mnp.repository.MnpRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallDetectionService : Service() {

    @Inject
    lateinit var repository: MnpRepository

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: MnpPhoneStateListener

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = MnpPhoneStateListener(this, repository)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY // Restart service if killed
    }

    private fun startForegroundService() {
        // Note: Modern Android versions (9+) restrict access to call state
        // This is the legacy approach that may not work on newer Android versions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                // This is the legacy approach - may not work on Android 9+
                // For newer Android versions, you would need to implement a CallScreeningService
                // which is a more complex solution that requires user to set the app as default
                // call screening app in system settings
                // For compatibility with older Android versions, we still use PhoneStateListener
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            } catch (securityException: SecurityException) {
                // Handle the case where permission is not granted at runtime
                // This can happen on some Android versions
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }
}