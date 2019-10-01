package com.example.downloadprocess.downloads.scheduling

sealed class TaskStatus {
    object Empty : TaskStatus()

    sealed class Pending : TaskStatus() {
        object Enqueued : Pending()
        object Blocked : Pending()
    }

    class Running(val progress: Double, val size: Long, val loaded: Long) : TaskStatus()

    sealed class Finished : TaskStatus() {
        object Succeeded : Finished()
        object Canceled : Finished()
        class Failed(val error: String?) : Finished()
    }
}
