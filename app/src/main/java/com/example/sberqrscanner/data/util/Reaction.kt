package com.example.sberqrscanner.data.util

sealed class Reaction<out T>{
    data class Success<out T>(val data: T): Reaction<T>()
    data class Error(val error: Exception): Reaction<Nothing>()
}
