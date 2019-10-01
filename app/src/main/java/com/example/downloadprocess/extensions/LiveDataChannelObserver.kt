package com.example.downloadprocess.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.channels.Channel

class LiveDataChannelObserver<T> : Observer<T?>, LifecycleObserver {

    val channel = Channel<T>()

    override fun onChanged(value: T?) {
        value?.let { channel.offer(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() = channel.close()
}
