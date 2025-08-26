package com.andy.qrscannerandy

import android.app.Application
import com.andy.qrscannerandy.di.koinModule
import com.andy.qrscannerandy.di.roomModule
import com.andy.qrscannerandy.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class QrScannerApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@QrScannerApp)
            modules(
                koinModule,
                roomModule,
                viewModelModule,
                )
        }
    }

}