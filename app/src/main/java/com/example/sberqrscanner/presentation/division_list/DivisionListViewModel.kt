package com.example.sberqrscanner.presentation.division_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.use_case.DivisionsUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DivisionListViewModel: ViewModel() {

    private val divisionUseCases = MyApp.instance!!.divisionUseCases

    private var _state = MutableStateFlow(DivisionListState())
    val state get() = _state.asStateFlow()

    private var _uiEvents = Channel<DivisionListUiEvent>()
    val uiEvents get() = _uiEvents.receiveAsFlow()

    private var getDivisionsJob: Job? = null

    init {
        loadDivisions()
    }

    fun onEvent(event: DivisionListEvent){
        when(event){
            is DivisionListEvent.DeleteDivision -> {
                deleteDivision(event.division)
            }
            is DivisionListEvent.InsertDivision -> {
                insertDivision(event.division)
            }
        }
    }

    private fun insertDivision(division: Division) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                loading = true
            )
            when (val res = divisionUseCases.insertDivision(division)) {
                is Reaction.Error -> {
                    _uiEvents.trySend(
                        DivisionListUiEvent.Error(res.error)
                    )
                }
                is Reaction.Success -> {

                }
            }
            _state.value = state.value.copy(
                loading = false
            )
        }
    }

    private fun deleteDivision(division: Division) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                loading = true
            )
            when (val res = divisionUseCases.deleteDivision(division)) {
                is Reaction.Success -> {
                    _uiEvents.trySend(
                        DivisionListUiEvent.DivisionDeleted(division)
                    )
                }
                is Reaction.Error -> {
                    _uiEvents.trySend(
                        DivisionListUiEvent.Error(res.error)
                    )
                }
            }
            _state.value = state.value.copy(
                loading = false
            )
        }
    }

    private fun loadDivisions() {
        getDivisionsJob?.cancel()
        getDivisionsJob = divisionUseCases.getDivisions()
            .onEach { reaction ->
                when(reaction){
                    is Reaction.Success -> {
                        _state.value = state.value.copy(
                            divisions = reaction.data,
                            loading = false
                        )
                    }
                    is Reaction.Error -> {
                        //TODO показ ошибки
                    }

                }

            }
            .launchIn(viewModelScope)
    }

}