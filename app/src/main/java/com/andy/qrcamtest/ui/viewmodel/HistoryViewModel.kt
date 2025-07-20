package com.andy.qrcamtest.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andy.qrcamtest.domain.model.QrScan
import com.andy.qrcamtest.domain.repository.QrRepository
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

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }

    fun deleteScan(id: Int) {
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }

    fun textIntent(url: String){
        viewModelScope.launch {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                getApplication<Application>().startActivity(intent)
            } catch (e: Exception) {
                // todo
            }
        }
    }

}
data class HistoryUiState(
    val scans: List<QrScan> = emptyList(),
    val isLoading: Boolean = true
)