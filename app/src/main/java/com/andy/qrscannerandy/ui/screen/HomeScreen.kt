package com.andy.qrscannerandy.ui.screen
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.andy.qrscannerandy.R
import com.andy.qrscannerandy.domain.helper.addCalendarEvent
import com.andy.qrscannerandy.domain.helper.callPhone
import com.andy.qrscannerandy.domain.helper.connectToWifi
import com.andy.qrscannerandy.domain.helper.openContact
import com.andy.qrscannerandy.domain.helper.openMap
import com.andy.qrscannerandy.domain.helper.sendEmailIntent
import com.andy.qrscannerandy.domain.helper.sendSms
import com.andy.qrscannerandy.domain.helper.urlIntent
import com.andy.qrscannerandy.domain.model.QrType
import com.andy.qrscannerandy.ui.viewmodel.QrViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: QrViewModel = koinViewModel(),
    navController: NavController
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    val hasAnimated by viewModel.hasAnimated.collectAsState()

    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(uiState.scanResultState) {
        if (uiState.scanResultState && !hasAnimated) { // Added a check to avoid re-triggering
            showAnimation = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (cameraPermissionState.status.isGranted) {
            if (showAnimation) {
                DataTransferAnimation {
                    // This block runs after the animation completes
                    showAnimation = false
                    viewModel.setAnimationShown()
                }
            } else if (!uiState.scanResultState) {
                // ✅ Show Camera Preview
                CameraPreview { qrText ->
                    if (!viewModel.state.value.scanResultState) {
                        viewModel.updateScanResult(qrText)
                    }
                }
            } else {
                // ✅ Show scan results
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (uiState.qrType) {
                        // ... your existing when statement
                        QrType.TEXT -> textQr(uiState.scanResult)
                        QrType.URL -> textUrl(uiState.scanResult) {
                            urlIntent(uiState.scanResult, context)
                        }
                        QrType.WIFI -> Button(onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                connectToWifi(uiState.scanResult, context)
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.android_support,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text(stringResource(R.string.connect_net))
                        }
                        QrType.EMAIL -> Button(onClick = {
                            viewModel.parseEmail(uiState.scanResult)?.let { emailData ->
                                sendEmailIntent(context, emailData)
                            } ?: Toast.makeText(context,R.string.invalid_email, Toast.LENGTH_SHORT).show()
                        }) {
                            Text(stringResource(R.string.send_email))
                        }
                        QrType.PHONE -> Button(onClick = {
                            callPhone(uiState.scanResult, context)
                        }) {
                            Text(stringResource(R.string.call))
                        }
                        QrType.SMS -> Button(onClick = {
                            sendSms(uiState.scanResult, context)
                        }) {
                            Text(stringResource(R.string.send_sms))
                        }
                        QrType.GEO -> Button(onClick = {
                            openMap(uiState.scanResult, context)
                        }) {
                            Text(stringResource(R.string.open_map))
                        }
                        QrType.CALENDAR -> Button(onClick = {
                            addCalendarEvent(uiState.scanResult, context)
                        }) {
                            Text(stringResource(R.string.add_calendar))
                        }
                        QrType.CONTACT -> {
                            openContact(uiState.scanResult, context)
                        }

                        QrType.OTHER -> textQr(uiState.scanResult)
                    }
                    Spacer(Modifier.size(20.dp))

                    Button(
                        onClick = {
                            viewModel.resetScan()
                            showAnimation = false
                        }, // Reiniciar cámara
                        modifier = Modifier
                            .height(60.dp)
                            .width(250.dp)
                    ) {
                        Text(stringResource(R.string.reset_scan))
                    }
                    Spacer(Modifier.size(10.dp))

                    Button(
                        onClick = { viewModel.shareContent(uiState.scanResult) },
                        modifier = Modifier
                            .height(60.dp)
                            .width(250.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.share_24),
                            contentDescription = stringResource(R.string.share_qr),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.share_qr))
                    }
                }
            }
        } else {
            Text(stringResource(R.string.camera_need_permission))
        }
    }
}

@Composable
fun textQr(text: String) {
    Text(
        text = text,
        fontSize = 30.sp
    )
}

@Composable
fun textUrl(
    text: String,
    textIntent: () -> Unit
) {
    Text(
        text = text,
        fontSize = 25.sp,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable {
                textIntent()
            }
    )
}
@Composable
fun DataTransferAnimation(
    onAnimationComplete: () -> Unit
) {
    val progressAnimatable = remember { Animatable(0f) }
    val expandAnimatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progressAnimatable.animateTo(
            1f,
            animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
        )
        delay(300)
        expandAnimatable.animateTo(
            1f,
            animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
        )
        delay(500)
        onAnimationComplete()
    }

    val progress = progressAnimatable.value
    val expandProgress = expandAnimatable.value

    val opacity = if (expandProgress > 0.5f) {
        1f - (expandProgress - 0.5f) * 2f
    } else {
        1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = 1f + expandProgress * 0.1f
                scaleY = 1f + expandProgress * 0.1f
                alpha = 1f - expandProgress
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scanBoxSize = 250.dp.toPx()
            val boxTopLeft = Offset(
                (size.width - scanBoxSize) / 2,
                (size.height - scanBoxSize) / 2
            )

            // ✅ Fondo negro detrás de la animación
            drawRect(
                color = Color.Black,
                topLeft = boxTopLeft,
                size = androidx.compose.ui.geometry.Size(scanBoxSize, scanBoxSize)
            )

            // Cuadrícula de puntos
            clipRect(
                left = boxTopLeft.x,
                top = boxTopLeft.y,
                right = boxTopLeft.x + scanBoxSize,
                bottom = boxTopLeft.y + scanBoxSize
            ) {
                val gridSize = 10.dp.toPx()
                for (x in 0 until (scanBoxSize / gridSize).toInt()) {
                    for (y in 0 until (scanBoxSize / gridSize).toInt()) {
                        drawCircle(
                            color = Color.Green.copy(alpha = Random.Default.nextFloat()),
                            center = Offset(
                                boxTopLeft.x + x * gridSize + gridSize / 2,
                                boxTopLeft.y + y * gridSize + gridSize / 2
                            ),
                            radius = 1.dp.toPx(),
                            alpha = opacity
                        )
                    }
                }
            }

            // Barra de carga con efecto glitch
            val barHeight = 8.dp.toPx()
            val barWidth = scanBoxSize * 0.8f
            val barY = boxTopLeft.y + scanBoxSize * 0.9f

            // The loading bar
            clipRect(
                left = boxTopLeft.x + (scanBoxSize - barWidth) / 2,
                top = barY,
                right = boxTopLeft.x + (scanBoxSize - barWidth) / 2 + barWidth * progress,
                bottom = barY + barHeight
            ) {
                // Glitch effect on the bar
                for (i in 0 until (barWidth * progress).toInt() step 5) {
                    val glitchHeight = Random.Default.nextInt(-3, 4)
                    drawLine(
                        color = Color.Green,
                        start = Offset(boxTopLeft.x + (scanBoxSize - barWidth) / 2 + i, barY + barHeight / 2 + glitchHeight),
                        end = Offset(boxTopLeft.x + (scanBoxSize - barWidth) / 2 + i + 5, barY + barHeight / 2 + glitchHeight),
                        strokeWidth = barHeight
                    )
                }
            }
        }

    }
}
