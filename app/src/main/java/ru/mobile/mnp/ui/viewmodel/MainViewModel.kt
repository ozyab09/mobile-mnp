package ru.mobile.mnp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.mobile.mnp.data.model.MnpNumber
import ru.mobile.mnp.repository.MnpRepository
import ru.mobile.mnp.util.MnpFileParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MnpRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState
    
    private val parser = MnpFileParser()
    
    fun checkNumber(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val normalizedNumber = parser.normalizePhoneNumber(phoneNumber)
                val result = repository.getMnpNumberByNumber(normalizedNumber)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    result = result,
                    lastUpdate = getLastUpdateDate(),
                    recordCount = getRecordCount()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private suspend fun getLastUpdateDate(): String {
        val metadata = repository.getMetadataByKey("last_update")
        return metadata?.value ?: "Unknown"
    }

    private suspend fun getRecordCount(): Int {
        return repository.getMnpNumbersCount()
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val result: MnpNumber? = null,
    val lastUpdate: String = "",
    val recordCount: Int = 0,
    val error: String? = null
)