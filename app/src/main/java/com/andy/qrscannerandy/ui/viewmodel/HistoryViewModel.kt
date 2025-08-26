package com.andy.qrscannerandy.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andy.qrscannerandy.domain.model.QrScan
import com.andy.qrscannerandy.domain.repository.QrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect


class HistoryViewModel(
    private val repository: QrRepository,
    application: Application,
): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllScans()
                .onEach { scans ->
                    _uiState.value = HistoryUiState(scans = scans, isLoading = false)
                }
                .catch {
                    _uiState.value = HistoryUiState(isLoading = false)
                }
                .collect()
        }
    }

    fun deleteScan(id: Int) {
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }

}
data class HistoryUiState(
    val scans: List<QrScan> = emptyList(),
    val isLoading: Boolean = true
)