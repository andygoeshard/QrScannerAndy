package com.andy.qrscannerandy.domain.manager

import android.net.Uri
import com.andy.qrscannerandy.domain.model.QrType

class QrManager{

    fun detectQrType(content: String): QrType {
        return when {
            content.startsWith("http://") || content.startsWith("https://") -> QrType.URL
            content.startsWith("WIFI:") -> QrType.WIFI
            content.startsWith("mailto:", ignoreCase = true) -> QrType.EMAIL
            content.startsWith("MATMSG:", ignoreCase = true) -> QrType.EMAIL
            content.startsWith("SMTP:", ignoreCase = true) -> QrType.EMAIL
            content.matches(Regex("^tel:.*", RegexOption.IGNORE_CASE)) -> QrType.PHONE
            content.startsWith("geo:", ignoreCase = true) -> QrType.GEO
            content.contains("BEGIN:VEVENT") -> QrType.CALENDAR
            content.contains("BEGIN:VCARD") -> QrType.CONTACT
            content.contains("SMSTO:", ignoreCase = true) -> QrType.SMS
            content.contains("sms:", ignoreCase = true) -> QrType.SMS
            else -> QrType.TEXT
        }
    }

    fun parseEmail(content: String): EmailData? {
        return when {
            content.startsWith("mailto:", true) -> {
                val uri = Uri.parse(content)
                val to = uri.schemeSpecificPart.substringBefore("?")
                val subject = uri.getQueryParameter("subject") ?: ""
                val body = uri.getQueryParameter("body") ?: ""
                EmailData(
                    java.net.URLDecoder.decode(to, "UTF-8"),
                    java.net.URLDecoder.decode(subject, "UTF-8"),
                    java.net.URLDecoder.decode(body, "UTF-8")
                )
            }
            content.startsWith("MATMSG:", true) -> {
                val to = Regex("TO:([^;]*)", RegexOption.IGNORE_CASE).find(content)?.groupValues?.get(1) ?: ""
                val subject = Regex("SUB:([^;]*)", RegexOption.IGNORE_CASE).find(content)?.groupValues?.get(1) ?: ""
                val body = Regex("BODY:([^;]*)", RegexOption.IGNORE_CASE).find(content)?.groupValues?.get(1) ?: ""
                EmailData(
                    java.net.URLDecoder.decode(to, "UTF-8"),
                    java.net.URLDecoder.decode(subject, "UTF-8"),
                    java.net.URLDecoder.decode(body, "UTF-8")
                )
            }
            content.startsWith("SMTP:", true) -> {
                val parts = content.removePrefix("SMTP:").split(":", limit = 3)
                val to = parts.getOrNull(0) ?: ""
                val subject = parts.getOrNull(1) ?: ""
                val body = parts.getOrNull(2) ?: ""
                EmailData(
                    java.net.URLDecoder.decode(to, "UTF-8"),
                    java.net.URLDecoder.decode(subject, "UTF-8"),
                    java.net.URLDecoder.decode(body, "UTF-8")
                )
            }
            else -> null
        }
    }

}

data class EmailData(val to: String, val subject: String, val body: String)
