package com.example.sberqrscanner.presentation.login

import java.lang.Exception

sealed class LoginUiEvent {
    class Error(e: Exception): LoginUiEvent()
    object LoginSuccesful: LoginUiEvent()
}
