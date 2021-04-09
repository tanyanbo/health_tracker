package com.example.helloworldapp.healthtracker.bloodPressure

import android.os.Bundle
import android.util.Log
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
import com.example.helloworldapp.healthtracker.addPerson.AddPersonFragment
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.databinding.FragmentBloodPressureBinding
import com.example.helloworldapp.healthtracker.databinding.FragmentHeightWeightBinding
import com.example.helloworldapp.healthtracker.dialogs.DeleteOneRowDialogBloodPressure
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import kotlinx.coroutines.launch

class BloodPressureFragment : Fragment() {

    private lateinit var binding: FragmentBloodPressureBinding
    private lateinit var adapter: BloodPressureRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_blood_pressure, container, false)

        val viewModelFactory = ViewModel.Factory(requireActivity().application)
        val viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModel::class.java)

        adapter = BloodPressureRecyclerViewAdapter(object :
            BloodPressureRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(bp: BloodPressure) {
                openDeleteOneRowDialog(bp)
            }
        })

        binding.bloodPressureRecyclerView.adapter = adapter

        lifecycleScope.launch {

            viewModel.setAllData()
            viewModel.job2.join()
            Log.i("MainActivity", "blood pressure fragment navigateToAddPerson: ${viewModel.navigateToAddPerson.value}")
            try {
                viewModel.bloodPressureAllDataOnePerson.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                    Log.i("Fragment", "list changed")
                })
            } catch (e: UninitializedPropertyAccessException) {
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.frameLayout, AddPersonFragment())
                    commit()
                }
            }
        }


        return binding.root
    }

    /**
     * Confirmation dialog to ask the user if he wants to delete one row of data
     */
    private fun openDeleteOneRowDialog(bp: BloodPressure) {
        val dialog = DeleteOneRowDialogBloodPressure(bp)
        dialog.show(parentFragmentManager, "dialog")
    }


}