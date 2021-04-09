package com.example.helloworldapp.healthtracker.bloodPressure

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
import com.example.helloworldapp.healthtracker.dialogs.DeleteAllBloodPressure
import com.example.helloworldapp.healthtracker.dialogs.DeleteAllGlucose
import com.example.helloworldapp.healthtracker.dialogs.DeleteOneRowDialogBloodPressure
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import kotlinx.coroutines.launch

class BloodPressureFragment : Fragment() {

    private lateinit var binding: FragmentBloodPressureBinding
    private lateinit var adapter: BloodPressureRecyclerViewAdapter
    private lateinit var viewModelFactory: ViewModel.Factory
    private lateinit var viewModel: ViewModel
    private lateinit var chosenPersonId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_blood_pressure, container, false)

        viewModelFactory = ViewModel.Factory(requireActivity().application)
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModel::class.java)

        adapter = BloodPressureRecyclerViewAdapter(object :
            BloodPressureRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(bp: BloodPressure) {
                openDeleteOneRowDialog(bp)
            }
        })

        viewModel.currentSelectedPersonId.observe(viewLifecycleOwner, Observer {
            chosenPersonId = it
            requireActivity().title = getString(R.string.title, it)
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

        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * Confirmation dialog to ask the user if he wants to delete one row of data
     */
    private fun openDeleteOneRowDialog(bp: BloodPressure) {
        val dialog = DeleteOneRowDialogBloodPressure(bp)
        dialog.show(parentFragmentManager, "dialog")
    }

    private fun openDeleteAllDialog() {
        DeleteAllBloodPressure(R.string.delete_dialog_message, chosenPersonId).show(parentFragmentManager, "dialog")
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
                viewModel.bloodPressureOrderByDateDesc()
                viewModel.bloodPressureAllDataDesc.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }

            // shows the data sorted by ascending date
            R.id.sortDateAscending -> {
                viewModel.bloodPressureOrderByDateAsc()
                viewModel.bloodPressureAllDataAsc.observe(viewLifecycleOwner, Observer {
                    adapter.submitList(it)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }


}