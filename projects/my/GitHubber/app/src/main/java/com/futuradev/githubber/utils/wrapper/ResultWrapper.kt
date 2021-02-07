package com.futuradev.githubber.utils.wrapper

sealed class ResultWrapper<out T> {

    data class Success<out T>(val value: T): ResultWrapper<T>()

    data class GenericError(val code: Int? = null,
                            val errorMessage: String? = null): ResultWrapper<Nothing>()

    object NetworkError: ResultWrapper<Nothing>()
}