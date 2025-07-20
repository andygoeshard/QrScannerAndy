package com.andy.qrcamtest.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.andy.qrcamtest.R
import com.andy.qrcamtest.ui.viewmodel.QrViewModel
import com.journeyapps.barcodescanner.ScanContract
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    viewModel: QrViewModel = koinViewModel(),
    navController: NavController
) {

    val uiState by viewModel.state.collectAsState()
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            viewModel.updateScanResult(result.contents)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(!uiState.scanResultState){
                Row{
                    TitleText("QR Scanner")
                }
                Spacer(modifier = Modifier.size(40.dp))
                Row {
                    cameraButton(
                        scanLauncher = { viewModel.scanQRCode(scanLauncher) },
                        modifierButton = Modifier
                            .height(200.dp)
                            .width(200.dp)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        modifierIcon = Modifier
                            .size(70.dp))
                }
                Spacer(modifier = Modifier.size(20.dp))
                Row{
                    TextUnderButton("↑ Click para escanear ↑")
                }
            }

            else {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    if(uiState.resultIsUrl){
                        textUrl(uiState.scanResult) {
                            viewModel.textIntent(uiState.scanResult)
                        }
                    }
                    else {
                        textQr(uiState.scanResult)
                    }
                }

                Spacer(Modifier.size(15.dp))

                Box(
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    cameraButton(
                            scanLauncher = { viewModel.scanQRCode(scanLauncher) },
                            modifierButton = Modifier
                                .height(100.dp)
                                .width(100.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .padding(16.dp),
                            modifierIcon = Modifier.size(60.dp)
                    )

                }
                Box(
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ){
                    TextUnderButton("↑ Escanear otra ve ↑")
                }

                Button(
                    onClick = { viewModel.shareContent(uiState.scanResult) },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .height(60.dp)
                        .width(250.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.share_24),
                        contentDescription = "Compartir",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir QR")
                }
            }
        }
    }
}

@Composable
fun textQr(text: String){

    Text(
        text = text,
        fontSize = 30.sp
    )

}

@Composable
fun TextUnderButton(text: String){
    Text(
        text = text,
        style = TextStyle(
            fontSize = 30.sp,
        )
    )
}

@Composable
fun textUrl(text: String,
            textIntent: () -> Unit){
    Text(
        text = text,
        color = Color.Cyan,
        fontSize = 25.sp,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable{
                textIntent()
        }
    )
}

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
        )
    )
}

@Composable
fun cameraButton(
        scanLauncher: () -> Unit,
        modifierButton: Modifier,
        modifierIcon: Modifier)
{

    Button(
        modifier = modifierButton,
        onClick = { scanLauncher() },
        shape = CircleShape,
    )
        {
        Icon(
            modifier = modifierIcon,
            painter = painterResource(R.drawable.camera_icon),
            contentDescription = "Scan")
    }

}


