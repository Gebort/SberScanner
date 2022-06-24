package com.example.sberqrscanner.presentation.division_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SharedDivisionViewModel: ViewModel() {

    private val deleteDivision = MyApp.instance!!.deleteDivision

    private val _state = MutableStateFlow(EditDivisionState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<EditDivisionUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEvent(event: DivisionEditEvent){
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
            is DivisionEditEvent.UpdateSelected -> {
                //TODO update
            }
        }
    }

}