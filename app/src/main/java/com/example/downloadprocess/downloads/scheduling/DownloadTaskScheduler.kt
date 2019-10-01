package com.example.downloadprocess.downloads.scheduling

import androidx.lifecycle.LiveData

interface DownloadTaskScheduler {

    fun geTaskStatus(uniqueWorkName: String): LiveData<TaskStatus>

    fun scheduleDownloadTask(url: String, path: String)

    fun clear(uniqueWorkName: String)
}
