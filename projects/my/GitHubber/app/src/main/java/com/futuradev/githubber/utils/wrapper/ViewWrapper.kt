package com.futuradev.githubber.utils.wrapper

sealed class ViewWrapper<out T> {

    data class Success <T> (val response : T) : ViewWrapper<T>()

    data class Failure(val messageId: Int) : ViewWrapper<Nothing>()

    object InProgress : ViewWrapper<Nothing>()
}

