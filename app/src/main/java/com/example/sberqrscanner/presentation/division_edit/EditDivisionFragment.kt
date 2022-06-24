package com.example.sberqrscanner.presentation.division_edit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.sberqrscanner.R
import com.example.sberqrscanner.databinding.FragmentDivisionListBinding
import com.example.sberqrscanner.databinding.FragmentEditDivisionBinding
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.division_list.DivisionListEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditDivisionFragment : Fragment() {

    private val model: SharedDivisionViewModel by activityViewModels()
    private var _binding: FragmentEditDivisionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDivisionBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonBack.setOnClickListener {
            popBack()
        }
        binding.buttonDelete.setOnClickListener {
            delete()
        }
        binding.buttonEdit.setOnClickListener {
            update()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                model.state.collect { state ->
                    if (state.selected == null){
                        popBack()
                    }

                    binding.textName.text = state.selected?.name
                        ?: getString(R.string.division_not_selected)

                    binding.buttonDownload.isEnabled = !state.loading && state.selected != null
                    binding.buttonDelete.isEnabled = !state.loading && state.selected != null
                    binding.buttonEdit.isEnabled = !state.loading && state.selected != null
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                binding.buttonBack.setOnClickListener {
                    findNavController().popBackStack()
                }
                model.uiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is EditDivisionUiEvent.Deleted -> {}
                        is EditDivisionUiEvent.Error -> {
                            Snackbar.make(
                                binding.textName,
                                R.string.error_happened,
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                }
                }
            }
        }
    }

    private fun delete(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.delete_division, model.state.value.selected?.name))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
                model.onEvent(DivisionEditEvent.DeleteSelected)
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("CheckResult")
    private fun update(){
        model.state.value.selected?.let {
            MaterialDialog(requireContext(), ).show {
                input(prefill = it.name) { _, text ->
                    model.onEvent(DivisionEditEvent.UpdateSelected(
                        it.copy(
                            name = text.toString()
                        )
                    ))
                }
                title(R.string.input_division_name)
                positiveButton(R.string.update)
                negativeButton(R.string.cancel)
            }
        }

    }

    private fun popBack(){
        findNavController().popBackStack()
    }
}