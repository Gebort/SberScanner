package com.example.sberqrscanner.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DivisionCheckViewModel: ViewModel() {

    private val getDivisions = MyApp.instance!!.getDivisions

    private var _state = MutableStateFlow(DivisionCheckState())
    val state get() = _state.asStateFlow()

    private var getDivisionsJob: Job? = null

    init {
        loadDivisions()
    }

    fun onEvent(event: DivisionCheckEvent){
        when (event) {
            is DivisionCheckEvent.checkDivision -> {
                val item = _state.value.divisions.find { it.division == event.divisionItem.division }
                item?.checked = true
                _state.value = state.value
            }
        }
    }

    private fun loadDivisions() {
        getDivisionsJob?.cancel()
        getDivisionsJob = getDivisions()
            .onEach { reaction ->
                when(reaction){
                    is Reaction.Success -> {
                        _state.value = state.value.copy(
                            divisions = reaction.data.map { division ->
                                val old = state.value.divisions.find { it.division == division }
                                DivisionItem(
                                    division = division,
                                    checked = old?.checked ?: false
                                )
                            },
                            checkedCount = state.value.divisions.count { it.checked }
                        )
                    }
                    is Reaction.Error -> {}

                }

            }
            .launchIn(viewModelScope)
    }

}