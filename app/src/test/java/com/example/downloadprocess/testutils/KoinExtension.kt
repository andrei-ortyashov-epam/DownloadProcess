package com.example.downloadprocess.testutils

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.koin.core.context.stopKoin

class KoinExtension : AfterEachCallback {

    override fun afterEach(context: ExtensionContext?) = stopKoin()
}
