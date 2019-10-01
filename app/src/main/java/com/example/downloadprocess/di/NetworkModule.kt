package com.example.downloadprocess.di

import com.example.downloadprocess.downloads.api.DownloadApi
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://dummy.com/"
const val CONNECT_TIMEOUT_SECONDS = 20L
const val READ_TIMEOUT_SECONDS = 30L

val networkModule = module {
    single { createHttpLogger() }
    single { createOkHttpBuilder(get()) }
    single { get<OkHttpClient.Builder>().build() }
    single { createWebService<DownloadApi>(get()) }
}

fun createOkHttpBuilder(
    httpLoggingInterceptor: HttpLoggingInterceptor,
    networkInterceptor: Interceptor = StethoInterceptor(),
    connectTimeout: Long = CONNECT_TIMEOUT_SECONDS,
    readTimeout: Long = READ_TIMEOUT_SECONDS
): OkHttpClient.Builder =
    OkHttpClient.Builder()
        .connectTimeout(connectTimeout, TimeUnit.SECONDS)
        .readTimeout(readTimeout, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .addNetworkInterceptor(networkInterceptor)

fun createHttpLogger(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BASIC
    }

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String = BASE_URL): T =
    getRetrofitBuilder(okHttpClient, url)
        .build()
        .create(T::class.java)

fun getRetrofitBuilder(okHttpClient: OkHttpClient, url: String): Retrofit.Builder =
    Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
