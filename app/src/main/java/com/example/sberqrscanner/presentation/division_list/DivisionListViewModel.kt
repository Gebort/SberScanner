package com.example.sberqrscanner.presentation.division_list

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.MainActivity
import com.example.sberqrscanner.presentation.scanner.DivisionCheckUiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DivisionListViewModel: ViewModel() {

    private val insertDivision = MyApp.instance!!.insertDivision
    private val getDivisions = MyApp.instance!!.getDivisions
    private val getProfile = MyApp.instance!!.getProfile
    private val exitProfile = MyApp.instance!!.exitProfile
    private val deleteAddress = MyApp.instance!!.deleteAddress

    private val profile = getProfile()

    private var _state = MutableStateFlow(DivisionListState(profile = profile))
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
                newDivision(event.name, event.division)
            }
            is DivisionListEvent.DeleteAddress -> {
                deleteAddress(event.activity)
            }
        }
    }

    private fun deleteAddress(activity: MainActivity){
        viewModelScope.launch {
            _state.update { it.copy(
                    loading = true
                ) }
            when (val reaction = deleteAddress(profile.address)){
                is Reaction.Success -> {
                    exitProfile(activity){
                        _uiEvents.trySend(DivisionListUiEvent.AddressDeleted)
                    }
                }
                is Reaction.Error -> {
                    _uiEvents.trySend(DivisionListUiEvent.Error(reaction.error))
                }
            }
        }
    }

    private fun newDivision(name: String, division: Division?) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                loading = true
            )
            when (val res = insertDivision(name, division)) {
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