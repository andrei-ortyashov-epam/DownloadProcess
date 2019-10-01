package com.example.downloadprocess.downloads.file

import timber.log.Timber
import java.io.*

class FileManagerImpl : FileManager {

    override fun writeFile(
        inputStream: InputStream,
        path: String,
        onProgress: (done: Long) -> Unit
    ): Boolean {
        lateinit var outputFile: File
        lateinit var outputStream: OutputStream
        try {
            outputFile = File(path)
            outputFile.parentFile.mkdirs()
            val fileReader = ByteArray(BUFFER_SIZE)
            outputStream = FileOutputStream(outputFile)
            var done = 0L
            var isReading = true
            while (isReading) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    done = -1
                    isReading = false
                } else {
                    done += read
                    outputStream.write(fileReader, 0, read)
                }
                onProgress(done)
            }
            outputStream.flush()
            return true
        } catch (e: IOException) {
            Timber.e(e)
            if (outputFile.exists()) {
                outputFile.delete()
            }
        } finally {
            inputStream.close()
            outputStream.close()
        }
        return false
    }

    override fun exists(path: String) = File(path).exists()

    companion object {
        private const val BUFFER_SIZE = 8094
    }
}
