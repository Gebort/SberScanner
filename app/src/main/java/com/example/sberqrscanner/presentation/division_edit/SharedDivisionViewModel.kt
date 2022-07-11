package com.example.sberqrscanner.presentation.division_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SharedDivisionViewModel: ViewModel() {

    private val deleteDivision = MyApp.instance!!.deleteDivision
    private val updateDivision = MyApp.instance!!.updateDivision

    private val _state = MutableStateFlow(EditDivisionState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<EditDivisionUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEvent(event: DivisionEditEvent) {
        when (event) {
            is DivisionEditEvent.Select -> {
                _state.value = state.value.copy(
                    selected = event.division
                )
            }
            is DivisionEditEvent.DeleteSelected -> {
                state.value.selected?.let {
                    viewModelScope.launch {
                        _state.update { it.copy(loading = true) }
                        when (val res = deleteDivision(it)) {
                            is Reaction.Success -> {
                                _uiEvents.emit(
                                    EditDivisionUiEvent.Deleted(it)
                                )
                            }
                            is Reaction.Error -> {
                                _uiEvents.emit(
                                    EditDivisionUiEvent.Error(res.error)
                                )
                            }
                        }
                        _state.update { it.copy(loading = false, selected = null) }
                    }
                }
            }
            is DivisionEditEvent.UpdateDivision -> {
                state.value.selected?.let {
                    viewModelScope.launch {
                        update(
                            event.division
                        )
                    }
                }
            }
        }
    }

    private suspend fun update(division: Division){
        _state.update { it.copy(loading = true) }
        when (val res = updateDivision(division)) {
            is Reaction.Success -> {
                _uiEvents.emit(
                    EditDivisionUiEvent.Changed(division)
                )
                _state.update { it.copy(
                    selected = res.data,
                    loading = false)
                }
            }
            is Reaction.Error -> {
                _uiEvents.emit(
                    EditDivisionUiEvent.Error(res.error)
                )
                _state.update { it.copy(
                    loading = false
                )
                }
            }
        }

    }

}