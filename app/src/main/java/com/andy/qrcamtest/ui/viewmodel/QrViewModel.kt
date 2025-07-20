package com.andy.qrcamtest.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.wifi.ScanResult
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.ImageCapture
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andy.qrcamtest.domain.manager.QrManager
import com.andy.qrcamtest.domain.model.QrScan
import com.andy.qrcamtest.domain.model.QrType
import com.andy.qrcamtest.domain.repository.QrRepository
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QrViewModel(
    private val qrManager: QrManager, application: Application, private val repository: QrRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(QRState())
    val state = _state.asStateFlow()

    fun scanQRCode(scanLauncher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>) {
        qrManager.scanQRCode(scanLauncher)
    }

    fun updateScanResult(result: String) {
        _state.value = _state.value.copy(
            scanResult = result, scanResultState = true, resultIsUrl = qrManager.isURL(result)
        )
        saveScan(result)
    }

    fun textIntent(url: String) {
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

    fun shareContent(text: String) {

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

}

data class QRState(

    val scanResult: String = "",
    val scanResultState: Boolean = false,
    val resultIsUrl: Boolean = false,
    val bitmap: Bitmap? = null

)
