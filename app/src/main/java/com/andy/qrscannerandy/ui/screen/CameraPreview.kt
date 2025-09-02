package com.andy.qrscannerandy.ui.screen

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.andy.qrscannerandy.R
import com.andy.qrscannerandy.domain.manager.QrCodeAnalyzer

@Composable
fun CameraPreview(
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    var torchEnabled by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context), QrCodeAnalyzer { result ->
                    onQrCodeScanned(result)
                })
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Vista de la cámara con gesto de zoom
        AndroidView(
            { previewView },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoomChange, _ ->
                        val newZoom = (zoomRatio * zoomChange).coerceIn(1f, camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 4f)
                        zoomRatio = newZoom
                        camera?.cameraControl?.setZoomRatio(zoomRatio)
                    }
                }
        )

        // ✅ Recuadro animado para escaneo
        val boxSize = 250.dp
        Box(
            modifier = Modifier
                .size(boxSize)
                .align(Alignment.Center)
                .border(3.dp, Color.Green, shape = RoundedCornerShape(16.dp))
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val lineOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = with(LocalDensity.current) { boxSize.toPx() },
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = Color.Green,
                    start = Offset(0f, lineOffset),
                    end = Offset(size.width, lineOffset),
                    strokeWidth = 4f
                )
            }
        }

        // ✅ Botón para encender/apagar la linterna
        Icon(
            painter = painterResource(id = if (torchEnabled) R.drawable.outline_flash_off_24 else R.drawable.outline_flash_on_24),
            contentDescription = "Flash",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .clickable {
                    torchEnabled = !torchEnabled
                    camera?.cameraControl?.enableTorch(torchEnabled)
                }
        )

        Text(
            text = stringResource(id =R.string.align_qr),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

