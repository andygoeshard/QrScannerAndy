package com.andy.qrscannerandy.ui.screen

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.andy.qrscannerandy.R
import com.andy.qrscannerandy.domain.helper.*
import com.andy.qrscannerandy.domain.manager.QrManager
import com.andy.qrscannerandy.domain.model.QrScan
import com.andy.qrscannerandy.domain.model.QrType
import com.andy.qrscannerandy.ui.viewmodel.HistoryViewModel
import com.andy.qrscannerandy.util.formatTimestamp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.scans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "\uD83D\uDCCB", // emoji de clipboard
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.no_history),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.scans.size) { index ->
                    val scan = state.scans[index]
                    QrCard(scan, viewModel)
                }
            }
        }
    }
}

@Composable
fun QrCard(scan: QrScan, viewModel: HistoryViewModel) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Botón de acción principal
            val mainActionButtonText = when (scan.type) {
                QrType.URL -> stringResource(R.string.open_url)
                QrType.WIFI -> stringResource(R.string.connect_net)
                QrType.EMAIL -> stringResource(R.string.send_email)
                QrType.SMS -> stringResource(R.string.send_sms)
                QrType.PHONE -> stringResource(R.string.call)
                QrType.GEO -> stringResource(R.string.open_map)
                QrType.CONTACT -> stringResource(R.string.add_contact)
                QrType.CALENDAR -> stringResource(R.string.add_calendar)
                else -> null
            }

            mainActionButtonText?.let {
                Button(
                    onClick = { executeQrIntent(scan, context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatQrContentForDisplay(scan),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.type, scan.type.name),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Text(
                text = formatTimestamp(scan.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Eliminar
                IconButton(
                    onClick = { viewModel.deleteScan(scan.id) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Botón Copiar
                IconButton(
                    onClick = { copyToClipboard(context, formatQrContentForDisplay(scan)) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_content_copy_24),
                        contentDescription = stringResource(R.string.copy),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Botón Compartir
                IconButton(
                    onClick = { viewModel.shareContent(formatQrContentForDisplay(scan)) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

fun executeQrIntent(scan: QrScan, context: Context) {
    when (scan.type) {
        QrType.URL -> urlIntent(scan.content, context)
        QrType.EMAIL -> sendEmailIntent(context, QrManager().parseEmail(scan.content))
        QrType.SMS -> sendSms(scan.content, context)
        QrType.PHONE -> callPhone(scan.content, context)
        QrType.GEO -> openMap(scan.content, context)
        QrType.CONTACT -> openContact(scan.content, context)
        QrType.WIFI -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) connectToWifi(scan.content, context)
        QrType.CALENDAR -> addCalendarEvent(scan.content, context)
        else -> {}
    }
}