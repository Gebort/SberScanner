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
import com.example.sberqrscanner.R
import com.example.sberqrscanner.databinding.FragmentDivisionListBinding
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.division_edit.DivisionEditEvent
import com.example.sberqrscanner.presentation.division_edit.EditDivisionUiEvent
import com.example.sberqrscanner.presentation.division_edit.SharedDivisionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DivisionListFragment : Fragment() {

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
                model.uiEvents.collect { event ->
                    when (event) {
                        is DivisionListUiEvent.Error -> {
                            Snackbar.make(
                                binding.recyclerView,
                                R.string.error_happened,
                                Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        is DivisionListUiEvent.DivisionDeleted -> {
                            Snackbar.make(
                                binding.recyclerView,
                                resources.getString(R.string.division_deleted, event.division.name),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(R.string.undo) {
                                    model.onEvent(DivisionListEvent.InsertDivision(event.division))
                                }
                                .show()
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
                            Snackbar.make(
                                binding.recyclerView,
                                resources.getString(R.string.division_deleted, uiEvent.division),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(R.string.undo) {
                                    model.onEvent(DivisionListEvent.InsertDivision(uiEvent.division))
                                }
                                .show()
                        }
                        else -> {}
                    }
                }
            }
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
                    Division(text.toString())
                ))
            }
            title(R.string.input_division_name)
            positiveButton(R.string.add)
            negativeButton(R.string.cancel)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DivisionListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DivisionListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}