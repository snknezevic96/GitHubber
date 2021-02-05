package com.futuradev.githubber.data.model

sealed class Status {
    object Success : Status()
    data class Failure(val message: String?) : Status()
}
