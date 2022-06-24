package com.example.sberqrscanner.presentation.scanner

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.databinding.FragmentScannerBinding
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionCheckListAdapter
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


private const val TAG = "SCANNER"

class ScannerFragment : Fragment() {

    private val bindCamera = MyApp.instance!!.bindCamera
    private val sharePdf = MyApp.instance!!.sharePdf
    private val generateReport = MyApp.instance!!.generateReport

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private val model: DivisionCheckViewModel by activityViewModels()
    private var adapter: DivisionCheckListAdapter? = null

    private val bottomPeekHeight = 325

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val view = binding.root

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = bottomPeekHeight
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewCheck.layoutManager = LinearLayoutManager(activity?.applicationContext)
        adapter = DivisionCheckListAdapter()
        binding.recyclerViewCheck.adapter = adapter
        binding.buttonEdit.setOnClickListener {
            val action = ScannerFragmentDirections.actionScannerFragmentToDivisionListFragment()
            findNavController().navigate(action)
        }
        binding.buttonMakeReport.setOnClickListener {
            val report = generateReport(model.state.value.divisions)
            when (sharePdf(report, requireActivity())){
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bindCamera(
                    viewLifecycleOwner,
                    requireContext(),
                    binding.previewView
                ).collect { scanResult ->
                    model.onEvent(DivisionCheckEvent.CheckDivision(scanResult))
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
                                Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    }
                }
            }
        }
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment ScannerFragment.
//         */
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ScannerFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}