package com.example.sberqrscanner.domain.use_case

import android.graphics.Bitmap
import com.example.sberqrscanner.domain.code_generator.CodeGenerator

class GenerateQRCode(
    private val generator: CodeGenerator
) {

    suspend operator fun invoke(data: String): Bitmap {
        return generator.generateCode(CodeGenerator.Option.QRCode, data)
    }

}