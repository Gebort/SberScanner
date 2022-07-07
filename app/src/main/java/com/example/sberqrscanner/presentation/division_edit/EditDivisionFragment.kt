package com.example.sberqrscanner.presentation.division_edit

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.databinding.FragmentEditDivisionBinding
import com.example.sberqrscanner.domain.use_case.CreateSnackbar
import com.example.sberqrscanner.presentation.division_edit.adapter.CodeImage
import com.example.sberqrscanner.presentation.division_edit.adapter.SliderTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


private const val REQUEST_STORAGE_PERMISSION_CODE = 2
private const val PAGES_MARGIN = 50
private const val PLACEHOLDER_WIDTH = 50
private const val PLACEHOLDER_HEIGHT = 50

class EditDivisionFragment : Fragment() {

    private val checkRequestPerm = MyApp.instance!!.checkRequestPerm
    private val generateQRCode = MyApp.instance!!.generateQRCode
    private val generateCode128 = MyApp.instance!!.generateCode128
    private val exportCode = MyApp.instance!!.exportCode
    private val shareCode = MyApp.instance!!.shareCode
    private val snackbar = MyApp.instance!!.createSnackbar

    private var oldState: EditDivisionState? = null
    private val model: SharedDivisionViewModel by activityViewModels()
    private var _binding: FragmentEditDivisionBinding? = null
    private val binding get() = _binding!!
    private val adapter = CodeSliderAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.buttonShare.setOnClickListener {
            sendCode()
        }
        val viewPager = binding.codeViewpager
        val tabLayout = binding.tabLayout
        viewPager.adapter = adapter

        val marginPageTransformer = MarginPageTransformer(PAGES_MARGIN)

        viewPager.setPageTransformer(CompositePageTransformer().also {
            it.addTransformer(marginPageTransformer)
            it.addTransformer(SliderTransformation())
        })

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            //Some implementation
        }.attach()

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
                            binding.buttonShare.isEnabled = false
                            renderCode(state.selected.id)
                        }

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
            MaterialDialog(requireContext()).show {
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

    private suspend fun getPlaceholder(): Bitmap {
        return Bitmap.createBitmap(
            PLACEHOLDER_WIDTH,
            PLACEHOLDER_HEIGHT,
            Bitmap.Config.ARGB_8888
        ).also {
            val canvas = Canvas(it)
            canvas.drawColor(Color.WHITE)
        }
    }

    private suspend fun renderCode(data: String){
        lifecycleScope.launch {
            val codeQR = async {
                return@async generateQRCode(data)
            }
            val codeBar = async {
                return@async generateCode128(data)
            }
            val background = async {
            return@async getPlaceholder()
        }
            val (back) = awaitAll(background)
            adapter.setBackground(back)
            val (qrCode, barcode) = awaitAll(codeQR, codeBar)
            val list = listOf(CodeImage(qrCode), CodeImage(barcode))
            adapter.changeList(list)
            binding.buttonShare.isEnabled = true
        }
    }

    private fun getCurrentCode(): Bitmap {
        return adapter.currentList[binding.codeViewpager.currentItem]
            .bitmap
    }

    private fun sendCode() {
        lifecycleScope.launch {
            if (
                checkRequestPerm(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    REQUEST_STORAGE_PERMISSION_CODE,
                    requireActivity()
                )
            ) {
                when (shareCode(getCurrentCode(), requireActivity())) {
                    is Reaction.Success -> {}
                    is Reaction.Error -> {
                        snackbar(
                            view = binding.codeViewpager,
                            type = CreateSnackbar.Large,
                            contentId = R.string.code_not_saved
                        )
                    }
                }
            }
        }
    }

    private fun downloadCode(){
            lifecycleScope.launch {
                if (
                    checkRequestPerm(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        REQUEST_STORAGE_PERMISSION_CODE,
                        requireActivity()
                    )
                ) {
                    when (val reaction = exportCode(getCurrentCode(), requireActivity())) {
                        is Reaction.Success -> {
                            snackbar(
                                view = binding.codeViewpager,
                                type = CreateSnackbar.Large,
                                contentStr = resources.getString(
                                    R.string.code_saved,
                                    reaction.data)
                            )
                        }
                        is Reaction.Error -> {
                            snackbar(
                                view = binding.codeViewpager,
                                type = CreateSnackbar.Large,
                                contentId = R.string.code_not_saved
                            )
                        }
                    }
                }
            }
    }

    private fun popBack(){
        findNavController().popBackStack()
    }
}