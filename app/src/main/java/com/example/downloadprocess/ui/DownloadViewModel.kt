package com.example.downloadprocess.ui

import android.Manifest
import androidx.lifecycle.LifecycleObserver
import com.example.downloadprocess.R
import com.example.downloadprocess.downloads.file.FileManager
import com.example.downloadprocess.downloads.scheduling.DownloadTaskScheduler
import com.example.downloadprocess.downloads.scheduling.TaskStatus
import com.example.downloadprocess.extensions.asChannel
import com.example.downloadprocess.providers.PathProvider
import com.example.downloadprocess.providers.ResourceProvider
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.coroutines.launch
import timber.log.Timber

class DownloadViewModel(
    private val downloadTaskScheduler: DownloadTaskScheduler,
    private val pathProvider: PathProvider,
    private val fileManager: FileManager,
    private val resourceProvider: ResourceProvider
) : BaseViewModel(), LifecycleObserver {

    private lateinit var rxPermissions: RxPermissions

    fun init(rxPermissions: RxPermissions) {
        this.rxPermissions = rxPermissions
    }

    fun startDownload(url: String) {
        addDisposable(
            rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                .subscribe({ onPermissionResult(it, url) }, Timber::e)
        )
    }

    private fun onPermissionResult(granted: Boolean, url: String) {
        if (granted) {
            val path = pathProvider.createPathFromUrl(url)
            val isAlreadyDownloaded = fileManager.exists(path)
            if (!isAlreadyDownloaded) {
                launch {
                    for (status in downloadTaskScheduler.geTaskStatus(url).asChannel()) {
                        when (status) {
                            is TaskStatus.Empty -> downloadTaskScheduler.scheduleDownloadTask(
                                url,
                                path
                            )
                            is TaskStatus.Finished.Failed -> _error.value = status.error
                            else -> Unit
                        }
                    }
                }
            }
        } else {
            _error.value = resourceProvider.getString(R.string.no_permissions_error)
        }
    }

    fun getDownloadProcess(url: String) = downloadTaskScheduler.geTaskStatus(url)
}

