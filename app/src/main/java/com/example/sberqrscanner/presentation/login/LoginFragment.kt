package com.example.sberqrscanner.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.example.sberqrscanner.R
import com.example.sberqrscanner.databinding.FragmentLoginBinding
import com.example.sberqrscanner.domain.login.Address
import com.example.sberqrscanner.domain.login.City
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val model: LoginViewModel by activityViewModels()
    private var addressesAdapter: AddressesAdapter? = null

    private var firstSet = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val cityList = mutableListOf<City>()
        val cityAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.city_item,
            cityList
        )

        with(binding.inputCity) {
            setAdapter(cityAdapter)
            threshold = 0
            validator = object : AutoCompleteTextView.Validator {
                override fun isValid(p0: CharSequence?): Boolean {
                    val c = model.state.value.cityOptions.cities.find { it.name == p0.toString() }
                    return c != null
                }

                override fun fixText(p0: CharSequence?): CharSequence {
                    return if (adapter.count > 0){
                        adapter.getItem(0).toString()
                    } else {
                        ""
                    }
                }
            }
            textChanged {  text ->
                onCityChange(text.toString())
            }

            setImeActionLabel(
                getString(R.string.ok),
                EditorInfo.IME_ACTION_DONE
            )

            setOnEditorActionListener { _, actionId, event ->
                var handled = false
                if (
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT
                ) {
                    performValidation()
                    handled = true
                }
                handled
            }
        }

        binding.recyclerViewAddresses.layoutManager = LinearLayoutManager(activity?.applicationContext)
        addressesAdapter = AddressesAdapter(
            onClickAddress =  ::onAddressSelect,
            onClickNew = ::onNewAddress
        )
        binding.recyclerViewAddresses.adapter = addressesAdapter

        binding.buttonLogin.setOnClickListener {
            tryLogin()
        }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    model.state.collectLatest { state ->

                         binding.buttonLogin.isEnabled = state.canBeValidated && !state.loading

                        binding.recyclerViewAddresses.isEnabled = !state.loading
                        binding.inputCity.isEnabled = !state.loading

                        with(cityList){
                            clear()
                            addAll(state.cityOptions.cities)
                        }
                        cityAdapter.notifyDataSetChanged()

                        if (binding.inputCity.text.toString() != state.cityStr){
                            binding.inputCity.setText(state.cityStr)
                        }

                        if (firstSet && state.cityOptions.cities.isNotEmpty()) {
                            binding.inputCity.setText(R.string.default_city)
                            firstSet = false
                        }

                        binding.layoutCity.error = if (state.error) "" else null

                        addressesAdapter?.changeList(
                            model.state.value.addressOption?.addresses ?: listOf(),
                            state.cityStr != ""
                        )

                    }
                }
            }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                model.uiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is LoginUiEvent.Error -> {
                            Snackbar.make(
                                binding.textView2,
                                R.string.error_happened,
                                Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        is LoginUiEvent.LoginSuccesful -> {
                            loginSuccess()
                        }
                    }
                }
            }
        }
        }

    private fun loginSuccess(){
        val action = LoginFragmentDirections
            .actionLoginFragmentToScannerFragment()
        findNavController().navigate(action)
    }

    private fun tryLogin(){
        model.onEvent(LoginEvent.TryLogin)
    }

    private fun onCityChange(selectedCity: String){
            model.onEvent(LoginEvent.CityChanged(selectedCity))
            addressesAdapter?.removeSelected()
        }

    private fun onAddressSelect(selectedAddress: Address){
        model.state.value.addressOption?.addresses?.let { addresses ->
            val addr = addresses.find { it.id == selectedAddress.id }
            if (addr != null) {
                model.onEvent(LoginEvent.AddressChanged(addr))
            }
            else{
                throw Exception("onAddressSelect: No address found")
            }
        }

    }

    private fun onNewAddress(){
        MaterialDialog(requireContext()).show {
            input { _, text ->
                model.onEvent(
                    LoginEvent.NewAddress(
                        city = binding.inputCity.text.toString(),
                        address = text.toString()
                ))
            }
            title(text = resources.getString(
                R.string.add_address_for_city,
                binding.inputCity.text.toString()))
            positiveButton(R.string.add)
            negativeButton(R.string.cancel)
        }
    }

    }
