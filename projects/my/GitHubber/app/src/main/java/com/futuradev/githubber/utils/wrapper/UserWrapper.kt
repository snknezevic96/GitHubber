package com.futuradev.githubber.utils.wrapper

sealed class UserWrapper<out T> {

    data class LoggedIn<out T>(val value: T): UserWrapper<T>()

    object NotLoggedIn: UserWrapper<Nothing>()
}
