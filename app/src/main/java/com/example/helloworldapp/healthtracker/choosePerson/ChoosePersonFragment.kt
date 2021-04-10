package com.example.helloworldapp.healthtracker.choosePerson

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureFragment
import com.example.helloworldapp.healthtracker.databinding.FragmentChoosePersonBinding
import com.example.helloworldapp.healthtracker.dialogs.DeleteAllBloodPressure
import com.example.helloworldapp.healthtracker.dialogs.DeleteOnePerson
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChoosePersonFragment : Fragment() {

    private lateinit var binding: FragmentChoosePersonBinding
    private lateinit var chosenPersonId: String
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_choose_person, container, false)

        val viewModelFactory = ViewModel.Factory(requireActivity().application)
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModel::class.java)




        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.deferredPersonList.await()
            }
            viewModel.personList.observe(viewLifecycleOwner, Observer {
                val personLst = it
                adapter = ArrayAdapter(
                    requireActivity(),
                    R.layout.spinner_first_view,
                    personLst
                )
                adapter.setDropDownViewResource(R.layout.spinner_dropdown)
                binding.personSpinner.adapter = adapter
            })
        }



        binding.personSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chosenPersonId = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        /**
         * Shows a toast message saying changed to xx person, changes the current selected person
         * and changes the screen to show the blood pressure fragment
         */
        binding.buttonGoToData.setOnClickListener {
            viewModel.changeCurrentSelectedPerson(chosenPersonId)
            requireActivity().title = getString(R.string.title, chosenPersonId)
            Toast.makeText(
                requireContext(),
                getString(R.string.person_changed_snackbar_message, "$chosenPersonId"),
                Toast.LENGTH_LONG
            ).show()
            viewModel.changeNavBarSelectedFunction()

            parentFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.translate_enter, R.anim.translate_exit)
                replace(R.id.frameLayout, BloodPressureFragment())
                addToBackStack(null)
                commit()
            }



        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_choose_person, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAllPeople) {
            openDeletePersonDialog()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun openDeletePersonDialog() {
        DeleteOnePerson(getString(R.string.delete_person, chosenPersonId), chosenPersonId, viewModel, requireActivity()).show(parentFragmentManager, "dialog")
    }
}


