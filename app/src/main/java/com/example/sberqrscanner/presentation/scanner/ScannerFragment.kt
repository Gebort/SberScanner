package com.example.sberqrscanner.presentation.scanner

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sberqrscanner.R
import com.example.sberqrscanner.databinding.FragmentScannerBinding
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionCheckListAdapter
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private val model: DivisionCheckViewModel by activityViewModels()
    private var adapter: DivisionCheckListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val view = binding.root

        activity?.title = ""

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = 225
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewCheck.layoutManager = LinearLayoutManager(activity?.applicationContext)
        adapter = DivisionCheckListAdapter()
        binding.recyclerViewCheck.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->

                    binding.textViewOutOf.text = resources.getString(
                        R.string.out_of_divisions,
                        state.checkedCount,
                        state.divisions.size
                    )

                    adapter?.changeList(state.divisions)
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val addAction: MenuItem = menu.findItem(R.id.action_add)
        addAction.isVisible = false
        val editAction: MenuItem = menu.findItem(R.id.action_editing)
        editAction.isVisible = false
    }

    private fun checkDivision(divisionItem: DivisionItem){
        model.onEvent(DivisionCheckEvent.checkDivision(divisionItem))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScannerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScannerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}