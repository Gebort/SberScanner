package com.example.sberqrscanner.domain.use_case

import android.graphics.Bitmap
import com.example.sberqrscanner.domain.code_generator.CodeGenerator

class GenerateCode128(
    private val generator: CodeGenerator
) {

    suspend operator fun invoke(data: String): Bitmap {
        return generator.generateCode(CodeGenerator.Option.Code128, data)
    }

}