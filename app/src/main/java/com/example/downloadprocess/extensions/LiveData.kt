package com.example.downloadprocess.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

fun <T> LiveData<T>.nonNullObserve(lifecycleOwner: LifecycleOwner, observer: (t: T) -> Unit) =
    observe(lifecycleOwner, Observer { it?.let(observer) })

fun <T> LiveData<T>.nonNullObserveForever(observer: (t: T) -> Unit) =
    observeForever(Observer { it?.let(observer) })

@ExperimentalCoroutinesApi
fun <T> LiveData<T>.asChannel(lifecycleOwner: LifecycleOwner): Channel<T> {
    val channelObserver = LiveDataChannelObserver<T>()
    observe(lifecycleOwner, channelObserver)
    lifecycleOwner.lifecycle.addObserver(channelObserver)
    channelObserver.channel.invokeOnClose { removeObserver(channelObserver) }
    return channelObserver.channel
}

@ExperimentalCoroutinesApi
fun <T> LiveData<T>.asChannel(): Channel<T> {
    val channelObserver = LiveDataChannelObserver<T>()
    observeForever(channelObserver)
    channelObserver.channel.invokeOnClose { removeObserver(channelObserver) }
    return channelObserver.channel
}
