package com.andy.qrscannerandy.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.andy.qrscannerandy.domain.manager.QrManager
import com.andy.qrscannerandy.domain.model.QrScan
import com.andy.qrscannerandy.domain.model.QrType
import com.andy.qrscannerandy.domain.repository.QrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.andy.qrscannerandy.domain.manager.EmailData
import kotlinx.coroutines.flow.StateFlow

class QrViewModel(
    private val qrManager: QrManager, application: Application, private val repository: QrRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(QRState())
    val state = _state.asStateFlow()

    private val _hasAnimated = MutableStateFlow(false)
    val hasAnimated: StateFlow<Boolean> = _hasAnimated.asStateFlow()

    fun updateScanResult(result: String) {
        _state.value = _state.value.copy(
            scanResult = result, scanResultState = true, qrType = qrManager.detectQrType(result)
        )
        saveScan(result)
    }

    fun setAnimationShown() {
        _hasAnimated.value = true
    }

    fun parseEmail(content: String): EmailData?{
        return qrManager.parseEmail(content)
    }

    private fun saveScan(content: String) {
        viewModelScope.launch {
            val qrScan = QrScan(
                content = content,
                type = qrManager.detectQrType(content),
                timestamp = System.currentTimeMillis()
            )
            repository.insertScan(qrScan)
        }
    }

    fun  shareContent(text: String) {

        val shareText = """
    📦 ¡Han compartido el contenido de un QR contigo!
    
    Contenido:
    🔗 $text
    
    Compartido desde QR Scanner de Andy 🟢
""".trimIndent()

        val context = getApplication<Application>().applicationContext
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val chooser = Intent.createChooser(intent, "Compartir QR con...")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun resetScan() {
        _state.update {
            it.copy(
                scanResult = "",
                scanResultState = false,
                qrType = QrType.OTHER
            )
        }
        _hasAnimated.value = false
    }


}

data class QRState(

    val scanResult: String = "",
    val scanResultState: Boolean = false,
    val qrType: QrType = QrType.OTHER,
    val bitmap: Bitmap? = null

)
