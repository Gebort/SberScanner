package com.example.sberqrscanner.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DivisionCheckViewModel: ViewModel() {

    private val getDivisions = MyApp.instance!!.getDivisions
    private val updateDivision = MyApp.instance!!.updateDivision
    private val writeProfileStorage = MyApp.instance!!.writeProfileStorage
    private val exitProfile = MyApp.instance!!.exitProfile
    private val dropChecks = MyApp.instance!!.dropChecks

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
            is DivisionCheckEvent.CheckDivisions -> {
                for (res in event.scans) {
                    val item = _state.value.divisions.find { it.id == res.id }
                    if (item != null ){
                        item.let {
                            if (!item.checked) {
                                _uiEvents.trySend(
                                    DivisionCheckUiEvent.DivisionChecked(item)
                                )
                                viewModelScope.launch {
                                    when (val reaction = updateDivision(item.copy(checked = true))) {
                                        is Reaction.Success -> {}
                                        is Reaction.Error -> {
                                            _uiEvents.trySend(
                                                DivisionCheckUiEvent.NetworkError(
                                                    reaction.error
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            else {
                                _uiEvents.trySend(
                                    DivisionCheckUiEvent.AlreadyChecked(
                                        item
                                    )
                                )
                            }
                        }
                    }
                    else {
                        _uiEvents.trySend(
                            DivisionCheckUiEvent.WrongCode
                        )
                    }
                    }
                }
            is DivisionCheckEvent.UncheckDivision -> {
                viewModelScope.launch {
                    when (val reaction = updateDivision(event.division.copy(checked = false))) {
                        is Reaction.Success -> {}
                        is Reaction.Error -> {
                            _uiEvents.trySend(DivisionCheckUiEvent.NetworkError(
                                reaction.error)
                            )
                        }
                    }
                }
            }
            is DivisionCheckEvent.DropChecks -> {
                viewModelScope.launch {
                    when(val reaction = dropChecks(state.value.divisions.filter { it.checked })) {
                        is Reaction.Success -> {}
                        is Reaction.Error -> {
                            _uiEvents.trySend(DivisionCheckUiEvent.NetworkError(
                                reaction.error)
                            )
                        }
                    }
                }
            }
            is DivisionCheckEvent.Logout -> {
                viewModelScope.launch {
                    writeProfileStorage(null)
                    _uiEvents.trySend(DivisionCheckUiEvent.Logout)
                    exitProfile(event.activity)
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
                            divisions = reaction.data
                        )
                    }
                    is Reaction.Error -> {
                        _uiEvents.trySend(DivisionCheckUiEvent.NetworkError(
                            reaction.error)
                        )
                    }
                }

            }
            .launchIn(viewModelScope)
    }

}