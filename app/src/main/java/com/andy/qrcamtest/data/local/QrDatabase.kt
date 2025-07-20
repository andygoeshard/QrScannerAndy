package com.andy.qrcamtest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [QrScanEntity::class], version = 1)
abstract class QrDatabase: RoomDatabase() {
    abstract fun qrScanDao(): QrScanDao
}