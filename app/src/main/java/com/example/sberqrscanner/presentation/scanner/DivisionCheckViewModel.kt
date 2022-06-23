package com.example.sberqrscanner.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.division_list.DivisionListUiEvent
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class DivisionCheckViewModel: ViewModel() {

    private val getDivisions = MyApp.instance!!.getDivisions

    private var _state = MutableStateFlow(DivisionCheckState())
    val state get() = _state.asStateFlow()

    private var _uiEvents = Channel<DivisionCheckUiEvent>()
    val uiEvents get() = _uiEvents.receiveAsFlow()

    private var getDivisionsJob: Job? = null

    init {
        loadDivisions()
    }

    fun onEvent(event: DivisionCheckEvent) {
        when (event) {
            is DivisionCheckEvent.checkDivision -> {
                val item = _state.value.divisions.find { it.division == event.division }
                item?.let {
                    if (!item.checked) {
                        val newList = state.value.divisions.toMutableList()
                        val index = newList.indexOf(item)
                        val newItem = item.copy(checked = true)
                        newList[index] = newItem
                        _state.value = state.value.copy(
                            divisions = newList
                        )
                        _uiEvents.trySend(
                            DivisionCheckUiEvent.DivisionChecked(event.division)
                        )
                    }
                }
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
                            }
                        )
                    }
                    is Reaction.Error -> {}

                }

            }
            .launchIn(viewModelScope)
    }

}