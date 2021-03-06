package com.example.helloworldapp.healthtracker.addPerson

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureFragment
import com.example.helloworldapp.healthtracker.choosePerson.ChoosePersonFragment
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabase
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabaseDao
import com.example.helloworldapp.healthtracker.database.glucose.Glucose
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabase
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabaseDao
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabase
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabaseDao
import com.example.helloworldapp.healthtracker.databinding.FragmentAddPersonBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddPersonFragment : Fragment() {

    private lateinit var binding: FragmentAddPersonBinding
    private lateinit var bloodPressureDataSource: BloodPressureDatabaseDao
    private lateinit var glucoseDataSource: GlucoseDatabaseDao
    private lateinit var heightWeightDataSource: HeightWeightDatabaseDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_person, container, false)

        bloodPressureDataSource =
            BloodPressureDatabase.getInstance(requireActivity().application).bloodPressureDatabaseDao
        glucoseDataSource =
            GlucoseDatabase.getInstance(requireActivity().application).glucoseDatabaseDao
        heightWeightDataSource =
            HeightWeightDatabase.getInstance(requireActivity().application).heightWeightDatabaseDao

        binding.etName.text = null

        val viewModelFactory =
            com.example.helloworldapp.healthtracker.viewModel.ViewModel.Factory(requireActivity().application)
        val viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(com.example.helloworldapp.healthtracker.viewModel.ViewModel::class.java)

        /**
         * Inserts a placeholder row of data to represent the person that has just been added,
         * this row of data will not be shown to the user. After inserting the data navigate to
         * the choose person screen.
         */
        binding.buttonSavePerson.setOnClickListener {
            viewModel.changeAppBarVisibility(true)
            viewModel.changeCurrentSelectedPerson(binding.etName.text.toString())
            requireActivity().title = getString(R.string.title, binding.etName.text.toString())
            val tempBp = BloodPressure(personId = binding.etName.text.toString(), isShown = false)
            val tempGlucose = Glucose(personId = binding.etName.text.toString(), isShown = false)
            val tempHeightWeight =
                HeightWeight(personId = binding.etName.text.toString(), isShown = false)
            lifecycleScope.launch {
                val deferred1 = async(Dispatchers.IO) {
                    bloodPressureDataSource.insert(tempBp)
                }
                val deferred2 = async(Dispatchers.IO) {
                    glucoseDataSource.insert(tempGlucose)
                }
                val deferred3 = async(Dispatchers.IO) {
                    heightWeightDataSource.insert(tempHeightWeight)
                }
                deferred1.await()
                deferred2.await()
                deferred3.await()
            }
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.frameLayout, ChoosePersonFragment())
                addToBackStack(null)
                commit()
            }


            binding.etName.text.clear()
        }
        binding.imgHeart.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            binding.imgHeart.setImageURI(data?.data)
        }
    }

}