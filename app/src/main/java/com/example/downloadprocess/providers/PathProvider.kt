package com.example.downloadprocess.providers

import java.io.File

class PathProvider(
    storageDir: String,
    appName: String
) {
    private val basePath = storageDir + File.separator + appName

    fun createPathFromUrl(url: String) =
        withBase(url.substring(url.lastIndexOf("/") + 1))

    private fun withBase(path: String) = basePath + File.separator + path
}
