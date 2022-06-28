package com.example.sberqrscanner.domain.code_generator

import android.graphics.Bitmap


interface CodeGenerator {

    suspend fun generateCode(option: Option, data: String): Bitmap

    sealed class Option {
        object QRCode: Option()
        object Code128: Option()
    }

}