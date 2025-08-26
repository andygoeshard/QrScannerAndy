package com.andy.qrscannerandy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrScanDao {
    @Query("SELECT * FROM qr_scan ORDER BY timestamp DESC")
    fun getAll(): Flow<List<QrScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qrScan: QrScanEntity)

    @Query("DELETE FROM qr_scan WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("UPDATE qr_scan SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int)
}