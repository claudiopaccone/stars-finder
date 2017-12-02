package it.personal.claudiopaccone.starsfinder.common

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import retrofit2.Response

class NotFoundException : Exception()

fun String.getNextUrl() =
        if (this.contains("rel=\"next\"") == false) null
        else this.substringAfter("<").substringBefore(">")

fun <T> Response<T>.handleErrorResponseCode() = when {
    code() == 404 -> throw NotFoundException()
    code() in (200..299) -> this
    else -> throw Exception()
}

fun Activity.closeKeyboard() {
    if (currentFocus == null) return

    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
}