package com.andy.qrcamtest

import android.app.Application
import com.andy.qrcamtest.di.koinModule
import com.andy.qrcamtest.di.roomModule
import com.andy.qrcamtest.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(
                koinModule,
                roomModule,
                viewModelModule,
                )
        }
    }

}