package com.andy.qrcamtest.domain.model

import com.andy.qrcamtest.data.local.QrScanEntity

fun QrScanEntity.toDomain(): QrScan = QrScan(
    id, content, QrType.valueOf(type), timestamp, isFavorite
)

fun QrScan.toEntity(): QrScanEntity = QrScanEntity(
    id, content, type.name, timestamp, isFavorite
)