package com.andy.qrscannerandy.di

import android.app.Application
import androidx.room.Room
import com.andy.qrscannerandy.data.local.QrDatabase
import com.andy.qrscannerandy.data.repository.QrRepositoryImpl
import com.andy.qrscannerandy.domain.manager.QrManager
import com.andy.qrscannerandy.domain.repository.QrRepository
import com.andy.qrscannerandy.ui.viewmodel.HistoryViewModel
import com.andy.qrscannerandy.ui.viewmodel.QrViewModel
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