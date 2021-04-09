package com.example.helloworldapp.healthtracker.glucose

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.glucose.Glucose
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import com.example.helloworldapp.healthtracker.databinding.FragmentGlucoseBinding
import com.example.helloworldapp.healthtracker.databinding.FragmentHeightWeightBinding
import com.example.helloworldapp.healthtracker.dialogs.DeleteOneRowDialogGlucose
import com.example.helloworldapp.healthtracker.heightWeight.HeightWeightRecyclerViewAdapter
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import kotlinx.coroutines.launch


class GlucoseFragment : Fragment() {

    private lateinit var adapter: GlucoseRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentGlucoseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_glucose, container, false)

        val viewModelFactory = ViewModel.Factory(requireActivity().application)
        val viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModel::class.java)

        adapter = GlucoseRecyclerViewAdapter(object :
            GlucoseRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(g: Glucose, adapterPosition: Int) {
                openDeleteOneRowDialog(g, adapter, adapterPosition)
            }
        })

        // changing the name might cause other problems, so I will continue using
        // bloodPressureRecyclerView even for this fragment
        binding.bloodPressureRecyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.setGlucoseData()
            viewModel.job3.join()
            viewModel.glucoseAllDataOnePerson.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }


        return binding.root
    }


    /**
     * Confirmation dialog to ask the user if he wants to delete one row of data
     */
    private fun openDeleteOneRowDialog(
        g: Glucose,
        adapter: GlucoseRecyclerViewAdapter,
        adapterPosition: Int
    ) {
        val dialog = DeleteOneRowDialogGlucose(g, adapter, adapterPosition)
        dialog.show(parentFragmentManager, "dialog")
    }


}