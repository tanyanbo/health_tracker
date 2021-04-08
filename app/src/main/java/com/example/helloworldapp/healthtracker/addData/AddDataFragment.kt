package com.example.helloworldapp.healthtracker.addData

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.ActionProvider
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabase
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabaseDao
import com.example.helloworldapp.healthtracker.database.glucose.Glucose
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabase
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabaseDao
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabase
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabaseDao
import com.example.helloworldapp.healthtracker.databinding.FragmentAddDataBinding
import com.example.helloworldapp.healthtracker.viewModel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class AddDataFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0

    var savedYear = year
    var savedMonth = month
    var savedDay = day
    var savedHour = hour
    var savedMinute = minute

    private lateinit var bloodPressureDataSource: BloodPressureDatabaseDao
    private lateinit var glucoseDataSource: GlucoseDatabaseDao
    private lateinit var heightWeightDataSource: HeightWeightDatabaseDao
    private lateinit var chosenPersonId: String

    val TAG = "AddDataFragment"

    private lateinit var binding: FragmentAddDataBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_data, container, false)


        binding.customTimeSwitch.isChecked = false

        bloodPressureDataSource =
            BloodPressureDatabase.getInstance(requireActivity().application).bloodPressureDatabaseDao
        glucoseDataSource =
            GlucoseDatabase.getInstance(requireActivity().application).glucoseDatabaseDao
        heightWeightDataSource =
            HeightWeightDatabase.getInstance(requireActivity().application).heightWeightDatabaseDao

        lifecycleScope.launch(Dispatchers.IO) {
            chosenPersonId = bloodPressureDataSource.getFirstPersonId()
        }


//        Log.i(TAG, "right after set ischecked: ${binding.customTimeSwitch.isChecked}")

        /**
         * The edit text for the date and time will only be shown if the customize date and time switch
         * is checked.
         */
        binding.customTimeSwitch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b) {
                binding.etDate.visibility = View.VISIBLE
            } else {
                binding.etDate.visibility = View.GONE
            }
        }

//        Log.i(TAG, "right after setonchecked change listener: ${binding.customTimeSwitch.isChecked}")

        /**
         * The user will be shown a dialog to pick a date when he clicks on the date and time edit text.
         */
        binding.etDate.setOnClickListener {
            pickDate()
        }

        val viewModelFactory = ViewModel.Factory(requireActivity().application)
        val viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(ViewModel::class.java)

        viewModel.currentSelectedPersonId.observe(viewLifecycleOwner, Observer {
            chosenPersonId = it
        })

        /**
         * This is deciding how to draw the edit text boxes, for example it will draw only
         * one edit text for glucose fragment since the user only needs to enter glucose,
         * for blood pressure it will draw three edit text boxes for high bp, low bp and heart
         * rate respectively.
         */
        viewModel.previousFragment.observe(viewLifecycleOwner, Observer {
            when (it) {
                1 -> {
                    binding.buttonSaveData.setOnClickListener {
                        onSaveButtonClickedBloodPressure()
                    }
                }
                2 -> {
                    binding.etBox1.hint = getString(R.string.enter_height)
                    binding.etBox2.hint = getString(R.string.enter_weight)
                    binding.etBox3.visibility = View.GONE
                    binding.buttonSaveData.setOnClickListener {
                        onSaveButtonClickedHeightWeight()
                    }
                }
                3 -> {
                    binding.etBox1.hint = getString(R.string.enter_glucose)
                    binding.etBox2.visibility = View.GONE
                    binding.etBox3.visibility = View.GONE
                    binding.buttonSaveData.setOnClickListener {
                        onSaveButtonClickedGlucose()
                    }
                }
            }
        })

//        Log.i(TAG, "right before return binding.root: ${binding.customTimeSwitch.isChecked}")
        return binding.root
    }

    /**
     * This is the function to get the current date and time. The date and time to be saved into the database
     * is also initialized to be the current date and time here. If the user enters another date and time then
     * saved date and time will be overwritten.
     */
    private fun getDateTime() {
        val currentDateTime = Calendar.getInstance()
        year = currentDateTime.get(Calendar.YEAR)
        month = currentDateTime.get(Calendar.MONTH)
        day = currentDateTime.get(Calendar.DAY_OF_MONTH)
        hour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        minute = currentDateTime.get(Calendar.MINUTE)

        savedYear = year
        savedMonth = month
        savedDay = day
        savedHour = hour
        savedMinute = minute
    }

    /**
     * This function shows the pick date dialog for the user to pick a date
     */
    private fun pickDate() {
        getDateTime()
        DatePickerDialog(requireContext(), this, year, month, day).show()
    }

    /**
     * This function shows the pick time dialog for the user to pick a time
     */
    private fun pickTime() {
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    /**
     * This is the function that is called after the user sets a date. The date to be saved into the
     * database is overwritten here. The function to open the pick time dialog is also called here so that
     * the user will see the pick time dialog after he chooses a date.
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedYear = year
        savedMonth = month
        savedDay = dayOfMonth
        pickTime()

    }

    /**
     * This is the function that is called after the user sets a time. The time to be saved into the database
     * is overwritten here. stringMinute is a variable that is used to set the minute to be shown to the user
     * after he selects a time. If the default savedMinute is used then the time will be shown as 10:5 when
     * its supposed to be 10:05.
     */
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        var stringMinute: String = ""
        val etDate: EditText = requireView().findViewById(R.id.etDate)
        if (savedMinute < 10) {
            stringMinute = ("0" + savedMinute.toString())
        } else {
            stringMinute = savedMinute.toString()
        }
        etDate.hint = "$savedYear-${savedMonth + 1}-$savedDay    $savedHour:$stringMinute"
    }

    private fun onSaveButtonClickedHeightWeight() {
        val dataToBeSaved = HeightWeight(
            personId = chosenPersonId,
            height = binding.etBox1.text.toString(),
            weight = binding.etBox2.text.toString()
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { heightWeightDataSource.insert(dataToBeSaved) }
        }
    }

    private fun onSaveButtonClickedGlucose() {
        val dataToBeSaved = Glucose(
            personId = chosenPersonId,
            glucose = binding.etBox1.text.toString()
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { glucoseDataSource.insert(dataToBeSaved) }
        }
    }

    private fun onSaveButtonClickedBloodPressure() {
        val dataToBeSaved = BloodPressure(
            personId = chosenPersonId,
            highBP = binding.etBox1.text.toString(),
            lowBP = binding.etBox2.text.toString(),
            heartRate = binding.etBox3.text.toString()
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { bloodPressureDataSource.insert(dataToBeSaved) }
        }
    }


}