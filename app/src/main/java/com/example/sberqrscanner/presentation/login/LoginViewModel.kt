package com.example.sberqrscanner.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Address
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.login.InvalidLoginException
import com.example.sberqrscanner.domain.login.Profile
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val validateProfile = MyApp.instance!!.validateProfile
    private val getCityOptions = MyApp.instance!!.getCityOptions
    private val getAddressesOptions = MyApp.instance!!.getAddressesOptions
    private val writeProfileStorage = MyApp.instance!!.writeProfileStorage

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<LoginUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var city: City? = null
    private var address: Address? = null

    private var cityOptionsJob: Job? = null
    private var addressOptionsJob: Job? = null
    private var validationJob: Job? = null

    init {
        loadCityOptions()
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.AddressChanged -> {
                address = event.address
                if (address != null && city != null) {
                    _state.update { it.copy(canBeValidated = true) }
                } else {
                    _state.update { it.copy(canBeValidated = false) }
                }
            }
            is LoginEvent.CityChanged -> {
                city = event.city
                address = null
                _state.update { it.copy(canBeValidated = false) }
                loadAddressOptions()
            }
            is LoginEvent.TryLogin -> {
                validate()
            }
        }
    }

    private fun validate() {
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            city?.let { city ->
                address?.let { address ->
                    _state.update { it.copy(loading = true) }
                    val profile = Profile(city, address)
                    when (val reaction = validateProfile(profile)){
                        is Reaction.Success -> {
                            async {
                                writeProfileStorage(profile)
                            }
                            _uiEvents.emit(LoginUiEvent.LoginSuccesful)
                        }
                        is Reaction.Error -> {
                            when (reaction.error) {
                                is InvalidLoginException -> {
                                    _state.update { it.copy(error = true, loading = false) }
                                }
                                else -> {
                                    _state.update { it.copy(loading = false) }
                                    _uiEvents.emit(LoginUiEvent.Error(reaction.error))
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private fun loadCityOptions(){
        cityOptionsJob?.cancel()
        cityOptionsJob = viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            getCityOptions().collect { reaction ->
                when (reaction) {
                    is Reaction.Success -> {
                        _state.value = state.value.copy(
                            cityOptions = reaction.data
                        )
                    }
                    is Reaction.Error -> {
                        _uiEvents.emit(LoginUiEvent.Error(reaction.error))
                    }
                }
                _state.update { it.copy(loading = false) }
            }
        }
    }

    private fun loadAddressOptions(){
            addressOptionsJob?.cancel()
            addressOptionsJob = viewModelScope.launch {
                if (city != null) {
                    city?.let { city ->
                        _state.update { it.copy(loading = true) }
                        getAddressesOptions(city).collect { reaction ->
                            when (reaction) {
                                is Reaction.Success -> {
                                    _state.value = state.value.copy(
                                        addressOption = reaction.data
                                    )
                                }
                                is Reaction.Error -> {
                                    _uiEvents.emit(LoginUiEvent.Error(reaction.error))
                                }
                            }
                            _state.update { it.copy(loading = false) }
                        }
                    }
                }
                else {
                _state.value = state.value.copy(
                    addressOption = null
                )
            }
            }
    }
}