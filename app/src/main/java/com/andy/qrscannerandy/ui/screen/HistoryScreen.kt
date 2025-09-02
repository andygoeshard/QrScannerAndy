package com.andy.qrscannerandy.ui.screen

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andy.qrscannerandy.ui.viewmodel.HistoryViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.andy.qrscannerandy.R
import androidx.navigation.NavController
import com.andy.qrscannerandy.domain.helper.addCalendarEvent
import com.andy.qrscannerandy.domain.helper.callPhone
import com.andy.qrscannerandy.domain.helper.connectToWifi
import com.andy.qrscannerandy.domain.helper.copyToClipboard
import com.andy.qrscannerandy.domain.helper.formatQrContentForDisplay
import com.andy.qrscannerandy.domain.helper.openContact
import com.andy.qrscannerandy.domain.helper.openMap
import com.andy.qrscannerandy.domain.helper.sendEmailIntent
import com.andy.qrscannerandy.domain.helper.sendSms
import com.andy.qrscannerandy.domain.helper.urlIntent
import com.andy.qrscannerandy.domain.manager.QrManager
import com.andy.qrscannerandy.domain.model.QrScan
import com.andy.qrscannerandy.domain.model.QrType
import com.andy.qrscannerandy.util.formatTimestamp

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (state.scans.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Podés agregar un ícono o emoji
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
                .padding(16.dp)
        ) {
            items(state.scans.size) { index ->
                val scan = state.scans[index]
                QrCard(scan, viewModel)
            }
        }
    }
}

@Composable
fun QrCard(scan: QrScan, viewModel: HistoryViewModel) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            when (scan.type) {
                QrType.URL -> CenteredActionButton(stringResource(R.string.open_url)) { executeQrIntent(scan, context) }
                QrType.WIFI -> CenteredActionButton(stringResource(R.string.connect_net)) { executeQrIntent(scan, context) }
                QrType.EMAIL -> CenteredActionButton(stringResource(R.string.send_email)) { executeQrIntent(scan, context) }
                QrType.SMS -> CenteredActionButton(stringResource(R.string.send_sms)) { executeQrIntent(scan, context) }
                QrType.PHONE -> CenteredActionButton(stringResource(R.string.call)) { executeQrIntent(scan, context) }
                QrType.GEO -> CenteredActionButton(stringResource(R.string.open_map)) { executeQrIntent(scan, context) }
                QrType.CONTACT -> CenteredActionButton(stringResource(R.string.add_contact)) { executeQrIntent(scan, context) }
                QrType.CALENDAR -> CenteredActionButton(stringResource(R.string.add_calendar)) { executeQrIntent(scan, context) }
                else -> {}
            }

            Text(
                text = formatQrContentForDisplay(scan),
                style = MaterialTheme.typography.bodyMedium
            )

            val message = context.getString(R.string.type,scan.type.name)

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = formatTimestamp(scan.timestamp),
                style = MaterialTheme.typography.bodySmall
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(4.dp))

            Row {
                TextButton(onClick = { viewModel.deleteScan(scan.id) }) {
                    Text(stringResource(R.string.delete))
                }
                TextButton(onClick = { copyToClipboard(context, formatQrContentForDisplay(scan)) }) {
                    Text(stringResource(R.string.copy))
                }

                TextButton(onClick = { viewModel.shareContent(formatQrContentForDisplay(scan)) }) {
                    Text(stringResource(R.string.share))
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

@Composable
fun CenteredActionButton(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Button(
            onClick = onClick,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(0.6f) // Botón ocupa 60% del ancho
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}