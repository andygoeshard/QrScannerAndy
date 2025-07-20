package com.andy.qrcamtest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andy.qrcamtest.domain.model.QrScan
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