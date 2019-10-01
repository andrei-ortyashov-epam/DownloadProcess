package com.example.downloadprocess.di

import android.content.Context
import android.os.Environment
import com.example.downloadprocess.downloads.file.FileManager
import com.example.downloadprocess.downloads.file.FileManagerImpl
import com.example.downloadprocess.extensions.appName
import com.example.downloadprocess.providers.PathProvider
import com.example.downloadprocess.providers.ResourceProvider
import org.koin.dsl.module

val coreModule = module {
    single { ResourceProvider(get()) }
    single<FileManager> { FileManagerImpl() }
    single {
        PathProvider(
            Environment.getExternalStorageDirectory().path,
            get<Context>().appName()
        )
    }
}
