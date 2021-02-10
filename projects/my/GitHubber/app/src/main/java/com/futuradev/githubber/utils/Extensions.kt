package com.futuradev.githubber.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.futuradev.githubber.utils.wrapper.ResultWrapper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Extensions

suspend inline fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    crossinline apiCall: suspend CoroutineScope.() -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke(this))
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError

                is HttpException -> ResultWrapper.GenericError(
                    throwable.code(),
                    throwable.message()
                )

                else -> ResultWrapper.GenericError(null, null)
            }
        }
    }
}

inline fun <T> ResultWrapper<T>.getResult(
    crossinline success: (value: T) -> Unit,
    noinline genericError: ((code: Int?, message: String?) -> Unit)? = null,
    noinline networkError: (() -> Unit)? = null
) {

    when(this) {
        is ResultWrapper.Success -> {
            success(value)
        }
        is ResultWrapper.GenericError -> {
            genericError?.let { it(code, errorMessage) }
        }
        is ResultWrapper.NetworkError -> {
            networkError?.let { it() }
        }
    }
}

fun View.showSnackMessage(message: String) {

    Snackbar
        .make(this, message, Snackbar.LENGTH_SHORT)
        .setAction("OK") {}
        .setActionTextColor(Color.YELLOW)
        .show()
}

fun String?.formatDate() : String? {
    this ?: return null

    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val outputDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val outputTime = SimpleDateFormat("HH:mm", Locale.getDefault())

    try {
        formatter.parse(this).also {
            it ?: return ""

            val date : String = outputDate.format(it)
            val time : String = outputTime.format(it)

            return "$date $time"
        }
    } catch (e: Exception) {
        return ""
    }
}

fun log(text: String) = Log.d("LOG:", text)

fun Activity.navigateTo(activity : Class<*>) {
    val intent = Intent(this, activity)
    startActivity(intent)
}