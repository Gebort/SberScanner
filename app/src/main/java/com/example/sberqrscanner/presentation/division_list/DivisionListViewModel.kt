package com.example.sberqrscanner.presentation.division_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DivisionListViewModel: ViewModel() {

    private val insertDivision = MyApp.instance!!.insertDivision
    private val getDivisions = MyApp.instance!!.getDivisions

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
            is DivisionListEvent.InsertDivision -> {
                newDivision(event.name, event.id)
            }
        }
    }

    private fun newDivision(name: String, id: String?) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                loading = true
            )
            when (val res = insertDivision(name, id)) {
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

    private fun loadDivisions() {
        getDivisionsJob?.cancel()
        getDivisionsJob = getDivisions()
            .onEach { reaction ->
                when(reaction){
                    is Reaction.Success -> {
                        _state.value = state.value.copy(
                            divisions = reaction.data,
                            loading = false
                        )
                    }
                    is Reaction.Error -> {
                        _uiEvents.trySend(DivisionListUiEvent.Error(reaction.error))
                        _state.value = state.value.copy(
                            loading = false
                        )
                    }

                }

            }
            .launchIn(viewModelScope)
    }

}