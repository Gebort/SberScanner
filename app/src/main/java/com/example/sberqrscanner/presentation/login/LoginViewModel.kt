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
import java.lang.IllegalArgumentException

class LoginViewModel: ViewModel() {

    private val validateProfile = MyApp.instance!!.validateProfile
    private val getCityOptions = MyApp.instance!!.getCityOptions
    private val getAddressesOptions = MyApp.instance!!.getAddressesOptions
    private val insertAddress = MyApp.instance!!.insertAddress
    private val insertCity = MyApp.instance!!.insertCity

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
                    _state.update { it.copy(
                        canBeValidated = true
                    ) }
                } else {
                    _state.update { it.copy(
                        canBeValidated = false
                    ) }
                }
            }
            is LoginEvent.CityChanged -> {
                city = state.value.cityOptions.cities.find { it.name == event.city }
                address = null
                _state.update { it.copy(
                    cityStr = event.city,
                    canBeValidated = false
                ) }
                loadAddressOptions()
            }
            is LoginEvent.TryLogin -> {
                validate()
            }
            is LoginEvent.NewAddress -> {
                newAddress(event.city, event.address)
            }
            else -> {
                throw IllegalArgumentException("Unsupported event")
            }
        }
    }

    private fun newAddress(cityStr: String, address: String){
        viewModelScope.launch {
            if (cityStr == ""){
                throw IllegalArgumentException("empty city string")
            }
            if (city != null) {
                city?.let { city ->
                    if (cityStr == city.name) {
                        _state.update { it.copy(loading = true) }
                        when (val reaction = insertAddress(city, address)) {
                            is Reaction.Success -> {
                            }
                            is Reaction.Error -> {
                                _uiEvents.tryEmit(LoginUiEvent.Error(reaction.error))
                            }
                        }
                    }
                    else {
                        throw Exception("cityStr != city.name")
                    }
                }
            }
            else {
                when (val reaction = insertCity(cityStr, address)){
                    is Reaction.Error -> {
                        _uiEvents.tryEmit(LoginUiEvent.Error(reaction.error))
                    }
                    is Reaction.Success -> {
                        city = reaction.data
                        _state.update { it.copy(cityStr = reaction.data.name) }
                        loadAddressOptions()
                    }
                }
            }

            _state.update { it.copy(loading = false) }

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
            getCityOptions().collect { reaction ->
                when (reaction) {
                    is Reaction.Success -> {
                        _state.value = state.value.copy(
                            cityOptions = reaction.data,
                        )
                    }
                    is Reaction.Error -> {
                        _uiEvents.emit(LoginUiEvent.Error(reaction.error))
                    }
                }
            }
        }
    }

    private fun loadAddressOptions(){
            addressOptionsJob?.cancel()
            addressOptionsJob = viewModelScope.launch {
                if (city != null) {
                    city?.let { city ->
                        getAddressesOptions(city).collect { reaction ->
                            when (reaction) {
                                is Reaction.Success -> {
                                    _state.update{ it.copy(
                                        addressOption = reaction.data
                                    ) }
                                }
                                is Reaction.Error -> {
                                    _uiEvents.emit(LoginUiEvent.Error(reaction.error))
                                }
                            }
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