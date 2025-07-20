package com.andy.qrcamtest.ui.screen

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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andy.qrcamtest.ui.viewmodel.HistoryViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.andy.qrcamtest.domain.model.QrType
import com.andy.qrcamtest.util.formatTimestamp

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        CircularProgressIndicator()
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(state.scans.size) { index ->
                val scan = state.scans[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        if (scan.type == QrType.URL) {
                            textUrl(scan.content) {
                                viewModel.textIntent(scan.content)
                            }
                        } else {
                            textQr(scan.content)
                        }
                        Text(
                            text = formatTimestamp(scan.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Divider()
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            TextButton(onClick = { viewModel.toggleFavorite(scan.id) }) {
                                Text(if (scan.isFavorite) "★ Fav" else "☆ Fav")
                            }
                            TextButton(onClick = { viewModel.deleteScan(scan.id) }) {
                                Text("🗑 Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}