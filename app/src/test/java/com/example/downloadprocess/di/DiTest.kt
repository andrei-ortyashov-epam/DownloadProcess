package com.example.downloadprocess.di

import android.content.Context
import com.example.downloadprocess.testutils.KoinExtension
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

@ExtendWith(KoinExtension::class)
class DiTest : KoinTest {

    @Test
    fun `checks modules`() {
        val appModule = module {
            single { mock<Context>() }
        }
        koinApplication {
            printLogger()
            modules(listOf(appModule) + appComponent)
        }.checkModules()
    }
}
