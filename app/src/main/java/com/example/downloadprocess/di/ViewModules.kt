package com.example.downloadprocess.di

import com.example.downloadprocess.ui.DownloadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { DownloadViewModel(get(), get(), get(), get()) }
}
