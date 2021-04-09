package com.example.helloworldapp.healthtracker.heightWeight

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureRecyclerViewAdapter
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import com.example.helloworldapp.healthtracker.databinding.FragmentHeightWeightBinding
import com.example.helloworldapp.healthtracker.dialogs.DeleteAllGlucose
import com.example.helloworldapp.healthtracker.dialogs.DeleteAllHeightWeight
import com.example.helloworldapp.healthtracker.dialogs.DeleteOneRowDialogHeightWeight
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import kotlinx.coroutines.launch


class HeightWeightFragment : Fragment() {

    private lateinit var adapter: HeightWeightRecyclerViewAdapter
    private lateinit var viewModelFactory: ViewModel.Factory
    private lateinit var viewModel: ViewModel
    private lateinit var chosenPersonId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHeightWeightBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_height_weight, container, false)

        viewModelFactory = ViewModel.Factory(requireActivity().application)
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModel::class.java)

        viewModel.currentSelectedPersonId.observe(viewLifecycleOwner, Observer {
            chosenPersonId = it
        })

        adapter = HeightWeightRecyclerViewAdapter(object :
            HeightWeightRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(hw: HeightWeight) {
                openDeleteOneRowDialog(hw)
            }
        })

        // changing the name might cause other problems, so I will continue using
        // bloodPressureRecyclerView even for this fragment
        binding.bloodPressureRecyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.setHeightWeightData()
            viewModel.job4.join()
            viewModel.heightWeightAllDataOnePerson.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Confirmation dialog to ask the user if he wants to delete one row of data
     */
    private fun openDeleteOneRowDialog(hw: HeightWeight) {
        val dialog = DeleteOneRowDialogHeightWeight(hw)
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun openDeleteAllDialog() {
        DeleteAllHeightWeight(R.string.delete_dialog_message, chosenPersonId).show(parentFragmentManager, "dialog")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_menu_item -> openDeleteAllDialog()

            // shows the data sorted by descending date
            R.id.sortDateDescending -> {
                viewModel.heightWeightOrderByDateDesc()
                viewModel.heightWeightAllDataDesc.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }

            // shows the data sorted by ascending date
            R.id.sortDateAscending -> {
                viewModel.heightWeightOrderByDateAsc()
                viewModel.heightWeightAllDataAsc.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

}