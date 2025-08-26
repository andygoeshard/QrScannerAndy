package com.andy.qrscannerandy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.andy.qrscannerandy.ui.screen.MainScreen
import com.andy.qrscannerandy.ui.theme.QrCamTestTheme

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


