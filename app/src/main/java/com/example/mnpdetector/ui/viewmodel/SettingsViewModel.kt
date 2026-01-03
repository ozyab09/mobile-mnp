package com.example.mnpdetector.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mnpdetector.repository.MnpRepository
import com.example.mnpdetector.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: MnpRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            val sourceUrl = preferencesHelper.getSourceUrl()
            val isAutoUpdateEnabled = preferencesHelper.isAutoUpdateEnabled()
            val updateFrequency = preferencesHelper.getUpdateFrequency()
            val wifiOnly = preferencesHelper.isWifiOnly()
            val chargingOnly = preferencesHelper.isChargingOnly()
            val overlayTimeout = preferencesHelper.getOverlayTimeout()
            val recordCount = repository.getMnpNumbersCount()
            val lastUpdate = repository.getMetadataByKey("last_update")?.value ?: "Never"
            
            _uiState.value = _uiState.value.copy(
                sourceUrl = sourceUrl,
                isAutoUpdateEnabled = isAutoUpdateEnabled,
                updateFrequency = updateFrequency,
                wifiOnly = wifiOnly,
                chargingOnly = chargingOnly,
                overlayTimeout = overlayTimeout.toString(),
                recordCount = recordCount,
                lastUpdate = lastUpdate
            )
        }
    }
    
    fun updateSourceUrl(url: String) {
        viewModelScope.launch {
            preferencesHelper.setSourceUrl(url)
            _uiState.value = _uiState.value.copy(sourceUrl = url)
        }
    }
    
    fun toggleAutoUpdate(enabled: Boolean) {
        viewModelScope.launch {
            preferencesHelper.setAutoUpdateEnabled(enabled)
            _uiState.value = _uiState.value.copy(isAutoUpdateEnabled = enabled)
        }
    }
    
    fun setUpdateFrequency(frequency: String) {
        viewModelScope.launch {
            preferencesHelper.setUpdateFrequency(frequency)
            _uiState.value = _uiState.value.copy(updateFrequency = frequency)
        }
    }
    
    fun setWifiOnly(enabled: Boolean) {
        viewModelScope.launch {
            preferencesHelper.setWifiOnly(enabled)
            _uiState.value = _uiState.value.copy(wifiOnly = enabled)
        }
    }
    
    fun setChargingOnly(enabled: Boolean) {
        viewModelScope.launch {
            preferencesHelper.setChargingOnly(enabled)
            _uiState.value = _uiState.value.copy(chargingOnly = enabled)
        }
    }
    
    fun setOverlayTimeout(timeout: String) {
        viewModelScope.launch {
            val timeoutInt = timeout.toIntOrNull() ?: 7
            preferencesHelper.setOverlayTimeout(timeoutInt)
            _uiState.value = _uiState.value.copy(overlayTimeout = timeoutInt.toString())
        }
    }
    
    fun getUpdateFrequencyOptions(): List<String> {
        return listOf("Never", "Weekly", "Bi-weekly", "Monthly")
    }
}

data class SettingsUiState(
    val sourceUrl: String = "https://files.bdpn.online/mobile_number_portability.zip",
    val isAutoUpdateEnabled: Boolean = false,
    val updateFrequency: String = "Weekly",
    val wifiOnly: Boolean = false,
    val chargingOnly: Boolean = false,
    val overlayTimeout: String = "7",
    val recordCount: Int = 0,
    val lastUpdate: String = "Never",
    val isLoading: Boolean = false
)