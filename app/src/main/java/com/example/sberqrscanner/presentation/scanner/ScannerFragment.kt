package com.example.sberqrscanner.presentation.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.example.sberqrscanner.BuildConfig
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.databinding.FragmentScannerBinding
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.scanner.ScanResult
import com.example.sberqrscanner.domain.use_case.CreateSnackbar
import com.example.sberqrscanner.presentation.MainActivity
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionCheckListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


private const val CAMERA_PERMISSION_REQUEST_CODE = 1
private const val REQUEST_STORAGE_PERMISSION_CODE = 2

class ScannerFragment : Fragment() {

    private val bindCamera = MyApp.instance!!.bindCamera
    private val sharePdf = MyApp.instance!!.sharePdf
    private val shareExcel = MyApp.instance!!.shareExcel
    private val generateReport = MyApp.instance!!.generateReport
    private val generateExcelReport = MyApp.instance!!.generateExcelReport
    private val checkRequestPerm = MyApp.instance!!.checkRequestPerm
    private val snackbar = MyApp.instance!!.createSnackbar

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private val model: DivisionCheckViewModel by activityViewModels()
    private var adapter: DivisionCheckListAdapter? = null

    private var lastToastShown = System.currentTimeMillis()
    private val toastDelay = 2000F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val view = binding.root

        BottomSheetBehavior.from(binding.bottomSheet).apply {
         //   peekHeight = bottomPeekHeight
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewCheck.layoutManager = LinearLayoutManager(activity?.applicationContext)
        adapter = DivisionCheckListAdapter(::checkDialog)
        binding.recyclerViewCheck.adapter = adapter
        binding.buttonEdit.setOnClickListener {
            val action = ScannerFragmentDirections.actionScannerFragmentToDivisionListFragment()
            findNavController().navigate(action)
        }
        binding.buttonMakeReport.setOnClickListener {
            reportTypeDialog()
        }
        binding.buttonRefresh.setOnClickListener {
            clearChecks()
        }
        binding.buttonExit.setOnClickListener {
            exitProfileDialog()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                    checkRequestPerm(
                        Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_REQUEST_CODE,
                        requireActivity()
                    )
                    bindCamera(
                        viewLifecycleOwner,
                        requireContext(),
                        binding.previewView
                    ).collect { scans ->
                        model.onEvent(DivisionCheckEvent.CheckDivisions(scans))
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->

                    binding.textViewOutOf.text = resources.getString(
                        R.string.out_of_divisions,
                        state.divisions.count { it.checked },
                        state.divisions.size
                    )

                    adapter?.changeList(state.divisions)
                    //adapter?.changeList(state.divisions)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.uiEvents.collect { uiEvent ->

                    when (uiEvent) {
                        is DivisionCheckUiEvent.DivisionChecked -> {
                            val toast = Toast.makeText(
                                requireContext(),
                                resources.getString(
                                    R.string.division_checked,
                                    uiEvent.division.name
                                ),
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                        is DivisionCheckUiEvent.Logout -> {
                            val action = ScannerFragmentDirections.actionGlobalLoginFragment()
                            findNavController().navigate(action)
                        }
                        is DivisionCheckUiEvent.NetworkError -> {
                            snackbar(
                                view = binding.imageView,
                                type = CreateSnackbar.Large,
                                contentId = R.string.error_happened,
                            )
                        }
                        is DivisionCheckUiEvent.WrongCode -> {
                            showCentralSnackbar(getString(R.string.incorrect_division))
                        }
                        is DivisionCheckUiEvent.AlreadyChecked -> {
                            showCentralSnackbar(resources.getString(
                                R.string.division_already_checked,
                                uiEvent.division.name
                            ))
                        }
                    }
                }
            }
        }
    }

    private fun showCentralSnackbar(string: String){
        if (System.currentTimeMillis() - lastToastShown > toastDelay){
            lastToastShown = System.currentTimeMillis()
            snackbar(
                view = binding.imageView,
                type = CreateSnackbar.SmallCenter,
                contentStr = string
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun reportTypeDialog(){
            if (
                checkRequestPerm(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    REQUEST_STORAGE_PERMISSION_CODE,
                    requireActivity()
                )
            ) {
                val choice = listOf(
                    getString(R.string.pdf_file),
                    getString(R.string.excel_file)
                )

                MaterialDialog(requireContext()).show {
                    listItemsSingleChoice(items = choice) { dialog, index, text ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            when (text) {
                                choice[0] -> {
                                    sendReport(Types.PDF)
                                }
                                choice[1] -> {
                                    sendReport(Types.EXCEL)
                                }
                            }
                        }
                    }
                    title(res = R.string.share_report)
                }
            }
        }

    private suspend fun sendReport(reportType: Types){
                val res = when (reportType) {
                    Types.PDF -> {
                        val report = generateReport(
                            model.state.value.divisions,
                            model.profile(),
                            requireContext()
                        )
                        sharePdf(report, requireActivity())
                    }
                    Types.EXCEL -> {
                        val report = generateExcelReport(
                            model.state.value.divisions,
                            model.profile(),
                            requireContext()
                        )
                        shareExcel(report, requireActivity())
                    }
                }
                when (res){
                    is Reaction.Success -> {}
                    is Reaction.Error -> {
                        Snackbar.make(
                            binding.previewView,
                            R.string.share_failed,
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                }
    }

    private fun clearChecks(){

        MaterialDialog(requireContext()).show {
            positiveButton(R.string.confirm) { dialog ->
                model.onEvent(DivisionCheckEvent.DropChecks)
            }
            negativeButton(R.string.cancel) {}
            title(R.string.drop_checks)
        }
    }

    @SuppressLint("CheckResult")
    fun checkDialog(division: Division){
        val textStr = "${division.number}. ${division.name}\n" +
                "${getString(R.string.floor, division.floor)}\n" +
                "${getString(R.string.wing, division.wing)}\n" +
                "${getString(R.string.fio, division.fio)}\n" +
                "${getString(R.string.phone, division.phone)}\n"
        MaterialDialog(requireContext()).show {
            checkBoxPrompt(
                text = getString(R.string.checked),
                isCheckedDefault = division.checked
            ) { checked ->
                if (checked) {
                    model.onEvent(DivisionCheckEvent.CheckDivisions(listOf(ScanResult(division.id))))
                }
                else {
                    model.onEvent(DivisionCheckEvent.UncheckDivision(division))
                }
            }
            title(text = textStr)
            positiveButton { R.string.confirm }
        }
    }

    private fun exitProfileDialog(){
        MaterialDialog(requireContext()).show {
            positiveButton(R.string.confirm) { dialog ->
                model.onEvent(DivisionCheckEvent.Logout(requireActivity() as MainActivity))
            }
            negativeButton(R.string.cancel) {}
            title(R.string.change_workplace)
        }
    }

    private companion object {
        enum class Types {
            PDF, EXCEL
        }
    }

}