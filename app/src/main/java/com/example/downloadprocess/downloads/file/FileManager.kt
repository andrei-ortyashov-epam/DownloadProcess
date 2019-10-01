package com.example.downloadprocess.downloads.file

import java.io.InputStream

interface FileManager {

    fun writeFile(
        inputStream: InputStream,
        path: String,
        onProgress: (done: Long) -> Unit
    ): Boolean

    fun exists(path: String): Boolean
}