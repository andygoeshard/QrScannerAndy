package com.andy.qrcamtest.di

import android.app.Application
import androidx.room.Room
import com.andy.qrcamtest.data.local.QrDatabase
import com.andy.qrcamtest.data.repository.QrRepositoryImpl
import com.andy.qrcamtest.domain.manager.QrManager
import com.andy.qrcamtest.domain.repository.QrRepository
import com.andy.qrcamtest.ui.viewmodel.HistoryViewModel
import com.andy.qrcamtest.ui.viewmodel.QrViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val koinModule = module {

    single { QrManager() }

}

val roomModule = module {
    single {
        Room.databaseBuilder(
            get<Application>(),
            QrDatabase::class.java,
            "qr_database"
        ).build()
    }

    single { get<QrDatabase>().qrScanDao() }

    single<QrRepository> { QrRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { QrViewModel(get(), get(), get()) }
    viewModel { HistoryViewModel(get(), get()) }
}