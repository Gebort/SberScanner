package com.example.sberqrscanner.presentation.division_edit

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.databinding.FragmentDivisionListBinding
import com.example.sberqrscanner.databinding.FragmentEditDivisionBinding
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.division_list.DivisionListEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val REQUEST_WRITE_PERMISSION_CODE = 2

class EditDivisionFragment : Fragment() {

    private val checkPermission = MyApp.instance!!.checkPermission
    private val requestPermission = MyApp.instance!!.requestPermission
    private val generateCode = MyApp.instance!!.generateCode
    private val exportCode = MyApp.instance!!.exportCode
    private val shareCode = MyApp.instance!!.shareCode

    private var oldState: EditDivisionState? = null
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
        binding.buttonDownload.setOnClickListener {
            downloadCode(binding.imageCode.drawable.toBitmap())
        }
        binding.buttonShare.setOnClickListener {
            sendCode(binding.imageCode.drawable.toBitmap())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                model.state.collect { state ->
                    if (state.selected == null){
                        oldState = state
                        popBack()
                    }
                    else {
                        binding.textName.text = state.selected.name

                        if (state.selected != oldState?.selected){
                            binding.buttonDownload.isEnabled = false
                            renderCode(state.selected.id)
                        }

                        binding.buttonDownload.isEnabled = !state.loading
                        binding.buttonDelete.isEnabled = !state.loading
                        binding.buttonEdit.isEnabled = !state.loading
                        binding.buttonShare.isEnabled = !state.loading

                        oldState = state
                    }
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
                        is EditDivisionUiEvent.Changed -> {
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
                            name = text.toString()
                    ))
                }
                title(R.string.input_division_name)
                positiveButton(R.string.update)
                negativeButton(R.string.cancel)
            }
        }

    }

    private suspend fun renderCode(data: String){
        binding.imageCode.setImageBitmap(generateCode(data))
        binding.buttonDownload.isEnabled = true
    }

    private fun sendCode(code: Bitmap){
        when (shareCode(code, requireActivity())) {
            is Reaction.Success -> {}
            is Reaction.Error -> {
                Snackbar.make(
                    binding.textName,
                    R.string.code_not_saved,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun downloadCode(code: Bitmap){
        if (checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            REQUEST_WRITE_PERMISSION_CODE,
            requireActivity()
        )){
            when (val reaction = exportCode(code, requireActivity())) {
                is Reaction.Success -> {
                    Snackbar.make(
                        binding.textName,
                        R.string.code_saved,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
                is Reaction.Error -> {
                    Snackbar.make(
                        binding.textName,
                        R.string.code_not_saved,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
        else {
            requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                REQUEST_WRITE_PERMISSION_CODE,
                requireActivity()
            )
        }
    }

    private fun popBack(){
        findNavController().popBackStack()
    }
}