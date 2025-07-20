package com.andy.qrcamtest.data.repository

import com.andy.qrcamtest.data.local.QrScanDao
import com.andy.qrcamtest.domain.model.QrScan
import com.andy.qrcamtest.domain.model.toDomain
import com.andy.qrcamtest.domain.model.toEntity
import com.andy.qrcamtest.domain.repository.QrRepository
import kotlinx.coroutines.flow.map

class QrRepositoryImpl(private val dao: QrScanDao) : QrRepository {
    override fun getAllScans() = dao.getAll().map { it.map { it.toDomain() } }

    override suspend fun insertScan(scan: QrScan) = dao.insert(scan.toEntity())

    override suspend fun deleteScan(id: Int) = dao.delete(id)

    override suspend fun toggleFavorite(id: Int,) = dao.toggleFavorite(id)

}