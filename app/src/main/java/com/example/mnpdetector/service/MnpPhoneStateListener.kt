package com.example.mnpdetector.service

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.example.mnpdetector.repository.MnpRepository
import com.example.mnpdetector.util.MnpFileParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent

class MnpPhoneStateListener(
    private val context: Context,
    private val repository: MnpRepository
) : PhoneStateListener() {
    
    private val parser = MnpFileParser()
    private var isIncomingCall = false
    
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                // Incoming call
                phoneNumber?.let { number ->
                    val normalizedNumber = parser.normalizePhoneNumber(number)
                    // Check if the number is in our MNP database
                    CoroutineScope(Dispatchers.IO).launch {
                        val mnpRecord = repository.getMnpNumberByNumber(normalizedNumber)
                        if (mnpRecord != null) {
                            // Number is ported, show overlay
                            isIncomingCall = true
                            val intent = Intent(context, OverlayService::class.java).apply {
                                putExtra("operator", mnpRecord.operator)
                                putExtra("number", normalizedNumber)
                            }
                            context.startService(intent)
                        }
                    }
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                // Call ended
                if (isIncomingCall) {
                    isIncomingCall = false
                    // Stop overlay service
                    val intent = Intent(context, OverlayService::class.java)
                    context.stopService(intent)
                }
            }
        }
    }
}