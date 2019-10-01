package com.example.downloadprocess.downloads.scheduling

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.downloadprocess.downloads.api.DownloadApi
import com.example.downloadprocess.downloads.file.FileManager
import com.example.downloadprocess.downloads.work.DownloadWorker

class WorkerFactoryImpl(
    private val downloadApi: DownloadApi,
    private val fileManager: FileManager
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ) =
        when (workerClassName) {
            DownloadWorker::class.java.canonicalName ->
                DownloadWorker(appContext, workerParameters, downloadApi, fileManager)

            else -> throw IllegalStateException("Unknown Worker class name - $workerClassName")
        }
}
