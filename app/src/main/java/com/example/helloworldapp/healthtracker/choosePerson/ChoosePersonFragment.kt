package com.example.helloworldapp.healthtracker.choosePerson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.databinding.FragmentChoosePersonBinding
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChoosePersonFragment : Fragment() {

    private lateinit var binding: FragmentChoosePersonBinding
    private lateinit var chosenPersonId: String
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_choose_person, container, false)

        val viewModelFactory = ViewModel.Factory(requireActivity().application)
        val viewModel =
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

        return binding.root
    }
}


