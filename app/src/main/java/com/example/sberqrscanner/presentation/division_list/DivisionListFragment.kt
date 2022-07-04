package com.example.sberqrscanner.presentation.division_list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R
import com.example.sberqrscanner.databinding.FragmentDivisionListBinding
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.use_case.CreateSnackbar
import com.example.sberqrscanner.presentation.MainActivity
import com.example.sberqrscanner.presentation.division_edit.DivisionEditEvent
import com.example.sberqrscanner.presentation.division_edit.EditDivisionUiEvent
import com.example.sberqrscanner.presentation.division_edit.SharedDivisionViewModel
import com.example.sberqrscanner.presentation.scanner.DivisionCheckEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DivisionListFragment : Fragment() {

    private val snackbar = MyApp.instance!!.createSnackbar

    private var _binding: FragmentDivisionListBinding? = null
    private val binding get() = _binding!!

    private val model: DivisionListViewModel by activityViewModels()
    private val modelSelect: SharedDivisionViewModel by activityViewModels()
    private var adapter: DivisionsListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDivisionListBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        adapter = DivisionsListAdapter(::selectDivision)
        binding.recyclerView.adapter = adapter
        binding.buttonAdd.setOnClickListener {
            insertDivisionByDialog()
        }
        binding.buttonDelete.setOnClickListener {
            deleteAddressDialog()
        }
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    if (state.loading){
                        binding.progressBar.isVisible = true
                    }
                    else {
                        binding.progressBar.isGone = true
                    }

                    binding.textAddress.text = resources.getString(
                        R.string.address,
                        state.profile.address.name
                    )
                    binding.textTotalDivisions.text = resources.getString(
                        R.string.total_divisions,
                        state.divisions.size
                    )

                    adapter?.changeList(state.divisions)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.uiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is DivisionListUiEvent.Error -> {
                            snackbar(
                                view = binding.recyclerView,
                                type = CreateSnackbar.Large,
                                contentId = R.string.error_happened
                            )
                        }
                        is DivisionListUiEvent.DivisionDeleted -> {
                            snackbar(
                                view = binding.recyclerView,
                                type = CreateSnackbar.LargeAction,
                                contentStr = resources.getString(
                                    R.string.division_deleted,
                                    uiEvent.division.name),
                                actionId = R.string.undo,
                            ) {
                                model.onEvent(DivisionListEvent.InsertDivision(
                                    name = uiEvent.division.name,
                                    id = uiEvent.division.id
                                ))
                            }
                        }
                        is DivisionListUiEvent.AddressDeleted -> {
                            logOut()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                modelSelect.uiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is EditDivisionUiEvent.Deleted -> {
                            snackbar(
                                view = binding.recyclerView,
                                type = CreateSnackbar.LargeAction,
                                contentStr = resources.getString(
                                    R.string.division_deleted,
                                    uiEvent.division.name),
                                actionId = R.string.undo,
                            ) {
                                model.onEvent(DivisionListEvent.InsertDivision(
                                    name = uiEvent.division.name,
                                    id = uiEvent.division.id
                                ))
                            }
                        }
                        else -> {
                            throw Exception("Unrealised type in when")
                        }
                    }
                }
            }
        }
    }

    private fun logOut(){
        val action = DivisionListFragmentDirections.actionGlobalLoginFragment()
        findNavController().navigate(action)
    }

    private fun deleteAddressDialog(){
        MaterialDialog(requireContext()).show {
            positiveButton(R.string.confirm) { dialog ->
                model.onEvent(DivisionListEvent.DeleteAddress(requireActivity() as MainActivity))
            }
            negativeButton(R.string.cancel)
            title(text = resources.getString(
                R.string.delete_address,
                model.state.value.profile.address
            ))
        }
    }

    private fun selectDivision(division: Division){
        modelSelect.onEvent(DivisionEditEvent.Select(division))
        val action = DivisionListFragmentDirections.actionDivisionListFragmentToEditDivisionFragment()
        findNavController().navigate(action)
    }

    @SuppressLint("CheckResult")
    private fun insertDivisionByDialog(){
        MaterialDialog(requireContext()).show {
            input { _, text ->
                model.onEvent(DivisionListEvent.InsertDivision(
                    text.toString()
                ))
            }
            title(R.string.input_division_name)
            positiveButton(R.string.add)
            negativeButton(R.string.cancel)
        }
    }
}