package com.example.downloadprocess.di

import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.downloadprocess.downloads.scheduling.DownloadTaskScheduler
import com.example.downloadprocess.downloads.scheduling.DownloadTaskSchedulerImpl
import com.example.downloadprocess.downloads.scheduling.WorkerFactoryImpl
import org.koin.dsl.module

val schedulerModule = module {
    single<WorkerFactory> { WorkerFactoryImpl(get(), get()) }
    single { WorkManager.getInstance(get()) }
    single<DownloadTaskScheduler> { DownloadTaskSchedulerImpl(get()) }
}
