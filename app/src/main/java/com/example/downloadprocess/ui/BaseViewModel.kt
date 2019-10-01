package com.example.downloadprocess.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    protected val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + CoroutineExceptionHandler { _, exception ->
            Timber.e(exception)
            _error.postValue(exception.message)
        }

    private val disposables = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) = disposables.add(disposable)

    override fun onCleared() {
        coroutineContext.cancelChildren()
        disposables.clear()
    }
}
