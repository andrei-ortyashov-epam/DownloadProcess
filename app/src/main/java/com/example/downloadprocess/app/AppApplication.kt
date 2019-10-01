package com.example.downloadprocess.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.example.downloadprocess.di.appComponent
import com.facebook.stetho.Stetho
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class AppApplication : Application(), Configuration.Provider {

    private val workerFactory: WorkerFactory by inject()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this)
        startKoin {
            androidContext(this@AppApplication)
            modules(appComponent)
        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
