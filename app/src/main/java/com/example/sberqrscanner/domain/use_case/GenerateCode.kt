package com.example.sberqrscanner.domain.use_case

import android.graphics.Bitmap
import com.example.sberqrscanner.domain.code_generator.CodeGenerator

class GenerateCode(
    private val generator: CodeGenerator
) {

    operator fun invoke(data: String): Bitmap {
        return generator.generateCode(data)
    }

}