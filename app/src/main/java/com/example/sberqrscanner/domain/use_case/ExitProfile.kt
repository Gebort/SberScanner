package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.domain.repository.DivisionRepository
import com.example.sberqrscanner.presentation.MainActivity

class ExitProfile(
    private val repository: DivisionRepository
) {

    operator fun invoke(activity: MainActivity) {
        repository.exitProfile()
        activity.viewModelStore.clear()

    }

}