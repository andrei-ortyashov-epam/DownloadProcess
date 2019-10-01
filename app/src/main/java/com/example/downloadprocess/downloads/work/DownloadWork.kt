package com.example.downloadprocess.downloads.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.downloadprocess.downloads.api.DownloadApi
import com.example.downloadprocess.downloads.file.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

/**
 * Downloads any file to sdcard using the [DownloadApi].
 */
class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val downloadApi: DownloadApi,
    private val fileManager: FileManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val url = inputData.getString(ARGS_URL)
            if (url.isNullOrBlank())
                return@withContext Result.failure(workDataOf(ARGS_ERROR to "Empty url"))
            val path = inputData.getString(ARGS_PATH)
            if (path.isNullOrBlank())
                return@withContext Result.failure(workDataOf(ARGS_ERROR to "Empty path"))
            val response = downloadApi.download(url).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    if (writeContentToFile(it, path))
                        return@withContext Result.success()
                }
            }
            Result.retry()
        }

    private fun writeContentToFile(body: ResponseBody, path: String): Boolean {
        val length = body.contentLength()
        return fileManager.writeFile(body.byteStream(), path) {
            val done = if (it == -1L) length else it
            setProgressAsync(
                workDataOf(
                    ARGS_PROGRESS to done.toDouble() * 100 / length,
                    ARGS_SIZE to length,
                    ARGS_LOADED to done
                )
            )
        }
    }

    companion object {
        const val ARGS_URL = "ARGS_URL"
        const val ARGS_PATH = "ARGS_PATH"

        const val ARGS_ERROR = "ERROR"

        const val ARGS_SIZE = "SIZE"
        const val ARGS_LOADED = "LOADED"
        const val ARGS_PROGRESS = "PROGRESS"
    }
}
