package com.erayerarslan.stepscape.core

sealed class Response<out T> {
    data object Init : Response<Nothing>()
    data object Loading : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val message: String) : Response<Nothing>()
}