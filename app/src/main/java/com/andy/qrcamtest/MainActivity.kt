package com.andy.qrcamtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.andy.qrcamtest.ui.screen.MainScreen
import com.andy.qrcamtest.ui.theme.QrCamTestTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrCamTestTheme{
                MainScreen()
            }
        }
    }
}


