package ru.mobile.mnp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject constructor(@ApplicationContext context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("mnp_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_SOURCE_URL = "source_url"
        private const val KEY_AUTO_UPDATE_ENABLED = "auto_update_enabled"
        private const val KEY_UPDATE_FREQUENCY = "update_frequency"
        private const val KEY_WIFI_ONLY = "wifi_only"
        private const val KEY_CHARGING_ONLY = "charging_only"
        private const val KEY_OVERLAY_TIMEOUT = "overlay_timeout"
    }
    
    fun getSourceUrl(): String {
        return prefs.getString(KEY_SOURCE_URL, "https://files.bdpn.online/mobile_number_portability.zip") ?: 
            "https://files.bdpn.online/mobile_number_portability.zip"
    }
    
    fun setSourceUrl(url: String) {
        prefs.edit { putString(KEY_SOURCE_URL, url) }
    }
    
    fun isAutoUpdateEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_UPDATE_ENABLED, false)
    }
    
    fun setAutoUpdateEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_AUTO_UPDATE_ENABLED, enabled) }
    }
    
    fun getUpdateFrequency(): String {
        return prefs.getString(KEY_UPDATE_FREQUENCY, "Weekly") ?: "Weekly"
    }
    
    fun setUpdateFrequency(frequency: String) {
        prefs.edit { putString(KEY_UPDATE_FREQUENCY, frequency) }
    }
    
    fun isWifiOnly(): Boolean {
        return prefs.getBoolean(KEY_WIFI_ONLY, false)
    }
    
    fun setWifiOnly(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_WIFI_ONLY, enabled) }
    }
    
    fun isChargingOnly(): Boolean {
        return prefs.getBoolean(KEY_CHARGING_ONLY, false)
    }
    
    fun setChargingOnly(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_CHARGING_ONLY, enabled) }
    }
    
    fun getOverlayTimeout(): Int {
        return prefs.getInt(KEY_OVERLAY_TIMEOUT, 7)
    }
    
    fun setOverlayTimeout(timeout: Int) {
        prefs.edit { putInt(KEY_OVERLAY_TIMEOUT, timeout) }
    }
}