package com.example.downloadprocess.downloads.scheduling

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.*
import com.example.downloadprocess.downloads.work.DownloadWorker
import com.example.downloadprocess.downloads.work.DownloadWorker.Companion.ARGS_ERROR
import com.example.downloadprocess.downloads.work.DownloadWorker.Companion.ARGS_LOADED
import com.example.downloadprocess.downloads.work.DownloadWorker.Companion.ARGS_PATH
import com.example.downloadprocess.downloads.work.DownloadWorker.Companion.ARGS_PROGRESS
import com.example.downloadprocess.downloads.work.DownloadWorker.Companion.ARGS_SIZE
import com.example.downloadprocess.downloads.work.DownloadWorker.Companion.ARGS_URL
import java.util.concurrent.TimeUnit

class DownloadTaskSchedulerImpl(
    private val workManager: WorkManager
) : DownloadTaskScheduler {

    override fun geTaskStatus(uniqueWorkName: String): LiveData<TaskStatus> =
        Transformations.map(
            workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName),
            ::mapTaskStatus
        )

    override fun clear(uniqueWorkName: String) {
        workManager.cancelUniqueWork(uniqueWorkName)
    }

    override fun scheduleDownloadTask(url: String, path: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val downloadWork = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(ARGS_URL to url, ARGS_PATH to path)
            )
            .setInitialDelay(0, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(url)
            .build()
        workManager.enqueueUniqueWork(url, ExistingWorkPolicy.APPEND, downloadWork)
    }

    private fun mapTaskStatus(workInfo: List<WorkInfo>): TaskStatus {
        if (workInfo.isNotEmpty()) {
            workInfo.firstOrNull { it.state == WorkInfo.State.RUNNING }?.also {
                return TaskStatus.Running(
                    progress = it.progress.getDouble(ARGS_PROGRESS, 0.0),
                    size = it.progress.getLong(ARGS_SIZE, 0),
                    loaded = it.progress.getLong(ARGS_LOADED, 0)
                )
            }
            if (workInfo.any { it.state == WorkInfo.State.BLOCKED }) {
                return TaskStatus.Pending.Blocked
            }
            if (workInfo.any { it.state == WorkInfo.State.ENQUEUED }) {
                return TaskStatus.Pending.Enqueued
            }
            if (workInfo.any { it.state == WorkInfo.State.CANCELLED }) {
                return TaskStatus.Finished.Canceled
            }
            workInfo.firstOrNull { it.state == WorkInfo.State.FAILED }?.also {
                return TaskStatus.Finished.Failed(
                    error = it.progress.getString(ARGS_ERROR)
                )
            }
            if (workInfo.all { it.state == WorkInfo.State.SUCCEEDED }) {
                return TaskStatus.Finished.Succeeded
            }
        }
        return TaskStatus.Empty
    }
}
