package com.andy.qrscannerandy.domain.helper

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.provider.ContactsContract
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.andy.qrscannerandy.domain.manager.EmailData
import com.andy.qrscannerandy.domain.model.QrScan
import com.andy.qrscannerandy.domain.model.QrType
import java.text.SimpleDateFormat
import java.util.Locale

fun parseVCard(qrContent: String): Pair<String?, String?> {
    val nameRegex = Regex("FN:(.*)", RegexOption.IGNORE_CASE)
    val phoneRegex = Regex("TEL(?:;[^:]*)?:(.*)", RegexOption.IGNORE_CASE)

    val name = nameRegex.find(qrContent)?.groupValues?.get(1)?.trim()
    val phone = phoneRegex.find(qrContent)?.groupValues?.get(1)?.trim()

    return name to phone
}

fun openContact(qrContent: String, context: Context) {
    val (name, phone) = parseVCard(qrContent)

    val intent = Intent(Intent.ACTION_INSERT).apply {
        type = ContactsContract.RawContacts.CONTENT_TYPE
        putExtra(ContactsContract.Intents.Insert.NAME, name)
        putExtra(ContactsContract.Intents.Insert.PHONE, phone)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir contactos", Toast.LENGTH_SHORT).show()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun connectToWifi(qrContent: String, context: Context) {
    val ssidRegex = Regex("S:([^;]+);")
    val typeRegex = Regex("T:([^;]+);")
    val passwordRegex = Regex("P:([^;]+);")

    val ssid = ssidRegex.find(qrContent)?.groupValues?.get(1)
    val password = passwordRegex.find(qrContent)?.groupValues?.get(1)
    val security = typeRegex.find(qrContent)?.groupValues?.get(1) // WPA/WPA2/etc

    if (ssid != null) {
        val specifierBuilder = WifiNetworkSpecifier.Builder().setSsid(ssid)

        if (!password.isNullOrEmpty()) {
            specifierBuilder.setWpa2Passphrase(password)
        }

        val wifiSpecifier = specifierBuilder.build()
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiSpecifier)
            .build()

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectivityManager.bindProcessToNetwork(network)
                Toast.makeText(context, "Conectado a $ssid", Toast.LENGTH_SHORT).show()
            }

            override fun onUnavailable() {
                Toast.makeText(context, "No se pudo conectar a $ssid", Toast.LENGTH_SHORT).show()
            }
        })
    } else {
        Toast.makeText(context, "QR de WiFi inválido", Toast.LENGTH_SHORT).show()
    }
}

fun callPhone(number: String, context: Context) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun sendEmailIntent(context: Context, emailData: EmailData?) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:${emailData?.to}")
        putExtra(Intent.EXTRA_SUBJECT, emailData?.subject)
        putExtra(Intent.EXTRA_TEXT, emailData?.body)
    }
    context.startActivity(intent)
}

fun openMap(geo: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geo))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}


fun urlIntent(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun sendSms(qrContent: String, context: Context) {
    val smsRegex = Regex("^SMSTO:([^:;]+):?(.*)?", RegexOption.IGNORE_CASE)
    val match = smsRegex.find(qrContent)

    val number = match?.groupValues?.get(1) ?: ""
    val message = match?.groupValues?.get(2) ?: ""

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$number")
        putExtra("sms_body", message)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir SMS", Toast.LENGTH_SHORT).show()
    }
}

fun addCalendarEvent(qrContent: String, context: Context) {
    val summary = Regex("SUMMARY:(.*)").find(qrContent)?.groupValues?.get(1) ?: "Evento"
    val description = Regex("DESCRIPTION:(.*)").find(qrContent)?.groupValues?.get(1) ?: ""
    val location = Regex("LOCATION:(.*)").find(qrContent)?.groupValues?.get(1) ?: ""

    val dtStartStr = Regex("DTSTART(?:;[^:]*)?:(\\d{8}T\\d{6}Z?)").find(qrContent)?.groupValues?.get(1)
    val dtEndStr = Regex("DTEND(?:;[^:]*)?:(\\d{8}T\\d{6}Z?)").find(qrContent)?.groupValues?.get(1)

    val formatter = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())

    val dtStart = dtStartStr?.let { formatter.parse(it)?.time } ?: System.currentTimeMillis()
    val dtEnd = dtEndStr?.let { formatter.parse(it)?.time } ?: dtStart + 60 * 60 * 1000 // +1h por default

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = Uri.parse("content://com.android.calendar/events")
        putExtra("title", summary)
        putExtra("description", description)
        putExtra("eventLocation", location)
        putExtra("beginTime", dtStart)
        putExtra("endTime", dtEnd)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo agregar el evento", Toast.LENGTH_SHORT).show()
    }
}

fun formatQrContentForDisplay(scan: QrScan): String {
    return when (scan.type) {
        QrType.WIFI -> {
            val ssid = Regex("S:([^;]+);").find(scan.content)?.groupValues?.get(1) ?: "N/A"
            "Red: $ssid"
        }
        QrType.PHONE -> {
            val number = scan.content.substringAfter("tel:")
            "Teléfono: $number"
        }
        QrType.CONTACT -> {
            val name = Regex("FN:(.*)").find(scan.content)?.groupValues?.get(1)?.trim() ?: "Sin nombre"
            val phone = Regex("TEL(?:;[^:]*)?:(.*)").find(scan.content)?.groupValues?.get(1)?.trim() ?: "Sin teléfono"
            "Contacto: $name\nTeléfono: $phone"
        }

        QrType.SMS -> {
            val smsRegex = Regex("^SMSTO:([^:;]+):?(.*)?", RegexOption.IGNORE_CASE)
            val match = smsRegex.find(scan.content)

            val number = match?.groupValues?.getOrNull(1) ?: "N/A"
            val message = match?.groupValues?.getOrNull(2) ?: ""

            "Número: $number\nMensaje: $message"
        }
        QrType.EMAIL -> {
            val to = scan.content.substringAfter("mailto:").substringBefore("?")
            val subject = Regex("subject=([^&]+)").find(scan.content)?.groupValues?.get(1)?.replace("+", " ") ?: ""
            "Para: $to\nAsunto: $subject"
        }
        QrType.GEO -> {
            val latLon = scan.content.substringAfter("geo:").substringBefore("?")
            "Ubicación: $latLon"
        }
        QrType.CALENDAR -> {
            val summary = Regex("SUMMARY:(.*)").find(scan.content)?.groupValues?.get(1) ?: "Evento"
            "Evento: $summary"
        }
        else -> scan.content
    }
}

