package com.andy.qrscannerandy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_scan")
data class QrScanEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,
    val type: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)