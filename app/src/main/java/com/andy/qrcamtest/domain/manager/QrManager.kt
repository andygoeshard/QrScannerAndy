package com.andy.qrcamtest.domain.manager

import android.util.Patterns
import androidx.activity.compose.ManagedActivityResultLauncher
import com.andy.qrcamtest.domain.model.QrType
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class QrManager{

    fun scanQRCode(
        scanLauncher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>,
    ) {
        val scanInitiate = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setOrientationLocked(true)
            setPrompt("Escanea un codigo QR")
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setCameraId(0)
        }
        scanLauncher.launch(scanInitiate)
    }

    fun isURL(string: String): Boolean{

        val pattern = Patterns.WEB_URL
        return pattern.matcher(string).matches()

    }

    fun detectQrType(content: String): QrType {
        return when {
            content.startsWith("http://") || content.startsWith("https://") -> QrType.URL
            content.startsWith("WIFI:") -> QrType.WIFI
            content.startsWith("mailto:") -> QrType.EMAIL
            content.matches(Regex("^tel:.*")) -> QrType.PHONE
            content.startsWith("geo:") -> QrType.GEO
            else -> QrType.TEXT
        }
    }

}