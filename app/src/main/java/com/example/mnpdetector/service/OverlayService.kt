package com.example.mnpdetector.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.mnpdetector.R
import com.example.mnpdetector.util.PreferencesHelper

class OverlayService : Service() {
    
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var timeoutMillis = 7000L // Default 7 seconds
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                stopSelf()
                return START_NOT_STICKY
            }
        }
        
        val operator = intent?.getStringExtra("operator") ?: return START_NOT_STICKY
        timeoutMillis = (intent.getIntExtra("timeout", 7) * 1000).toLong()
        
        showOverlay(operator)
        
        // Auto-hide after timeout
        Thread {
            Thread.sleep(timeoutMillis)
            hideOverlay()
        }.start()
        
        return START_NOT_STICKY
    }
    
    private fun showOverlay(operator: String) {
        if (overlayView != null) return // Already showing
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = layoutInflater.inflate(R.layout.overlay_call_widget, null)
        
        val textView = overlayView?.findViewById<TextView>(R.id.tvOverlayText)
        textView?.text = getString(R.string.ported_call_notification, operator)
        
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
        }
        
        // Position at bottom
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.x = 0
        params.y = 100
        
        windowManager?.addView(overlayView, params)
    }
    
    private fun hideOverlay() {
        overlayView?.let { view ->
            windowManager?.removeView(view)
            overlayView = null
        }
        stopSelf()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        hideOverlay()
    }
}