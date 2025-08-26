package com.andy.qrscannerandy.domain.repository

import com.andy.qrscannerandy.domain.model.QrScan
import kotlinx.coroutines.flow.Flow

interface QrRepository {
    fun getAllScans(): Flow<List<QrScan>>
    suspend fun insertScan(scan: QrScan)
    suspend fun deleteScan(id: Int)
    suspend fun toggleFavorite(id: Int)
}