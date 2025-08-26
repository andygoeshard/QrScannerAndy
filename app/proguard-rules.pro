# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# proguard-rules.pro

########################################
# Koin (usa reflexión para inyección)
########################################
-keep class org.koin.** { *; }
-keep class * extends org.koin.core.component.KoinComponent

########################################
# Kotlinx Serialization
########################################

# Mantener todos los serializer generados
-keep class **$$serializer { *; }

# Mantener Companion que provee serializers
-keepclassmembers class ** {
    public static ** Companion;
}

# Mantener las clases internas de kotlinx.serialization
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Evitar que se obfusquen las clases usadas por Serializers
-keepclassmembers class kotlinx.serialization.** { *; }
-keep class kotlinx.serialization.** { *; }

# Ktor internals
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }

########################################
# ZXing (JourneyApps y Core)
########################################
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *; }
-keepclassmembers class com.google.zxing.** { *; }

-dontwarn com.journeyapps.barcodescanner.**
-keep class com.journeyapps.barcodescanner.** { *; }

########################################
# Room (ya trae consumer-proguard-rules.pro, pero reforzamos)
########################################
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase

########################################
# CameraX (usa reflexión en algunos bindings)
########################################
-dontwarn androidx.camera.**
-keep class androidx.camera.** { *; }

########################################
# Jetpack Compose (ya trae reglas, reforzamos preview/dev tools)
########################################
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

########################################
# Navigation Compose
########################################
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

########################################
# Coil
########################################
-keep class coil.** { *; }
-dontwarn coil.**

########################################
# Google Ads SDK
########################################
-keep class com.google.android.libraries.ads.** { *; }
-dontwarn com.google.android.libraries.ads.**

########################################
# DataStore (usa protos/serialization)
########################################
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

########################################
# General
########################################
-dontwarn kotlinx.coroutines.**
-keepattributes Signature,InnerClasses,EnclosingMethod
