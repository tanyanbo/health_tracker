package com.example.helloworldapp.healthtracker.heightWeight

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
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureRecyclerViewAdapter
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import com.example.helloworldapp.healthtracker.databinding.FragmentHeightWeightBinding
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import kotlinx.coroutines.launch


class HeightWeightFragment : Fragment() {

    private lateinit var adapter: HeightWeightRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHeightWeightBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_height_weight, container, false)

        val viewModelFactory = ViewModel.Factory(requireActivity().application)
        val viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModel::class.java)

        adapter = HeightWeightRecyclerViewAdapter(object :
            HeightWeightRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(hw: HeightWeight) {
                Toast.makeText(requireContext(), "delete button tapped", Toast.LENGTH_SHORT).show()
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
        return binding.root
    }

}