package com.example.sberqrscanner.domain.code_generator

import android.graphics.Bitmap


interface CodeGenerator {

    fun generateCode(data: String): Bitmap

}