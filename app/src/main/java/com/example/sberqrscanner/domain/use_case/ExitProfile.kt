package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.domain.repository.DivisionRepository
import com.example.sberqrscanner.presentation.MainActivity

class ExitProfile(
    private val repository: DivisionRepository,
    private val writeProfileStorage: WriteProfileStorage
) {

    suspend operator fun invoke(activity: MainActivity, onEscape: (() -> Unit)? = null) {
        repository.exitProfile()
        writeProfileStorage(null)
        if (onEscape != null) {
            onEscape()
        }
        activity.viewModelStore.clear()

    }

}