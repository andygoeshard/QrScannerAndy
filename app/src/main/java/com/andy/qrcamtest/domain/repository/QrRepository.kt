package com.andy.qrcamtest.domain.repository

import com.andy.qrcamtest.domain.model.QrScan
import kotlinx.coroutines.flow.Flow

interface QrRepository {
    fun getAllScans(): Flow<List<QrScan>>
    suspend fun insertScan(scan: QrScan)
    suspend fun deleteScan(id: Int)
    suspend fun toggleFavorite(id: Int)
}