package com.andy.qrscannerandy.domain.model

data class QrScan(
    val id: Int = 0,
    val content: String,
    val type: QrType,
    val timestamp: Long,
    val isFavorite: Boolean = false
)

enum class QrType {
    TEXT, URL, WIFI, EMAIL, PHONE, GEO, CALENDAR, CONTACT, SMS,OTHER
}
