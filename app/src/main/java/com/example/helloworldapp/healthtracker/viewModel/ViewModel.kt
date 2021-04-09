package com.example.helloworldapp.healthtracker.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabase
import kotlinx.coroutines.*

class ViewModel(application: Application) : AndroidViewModel(application) {

    // variable to store which is the current fragment
    private val _previousFragment = MutableLiveData<Int>()
    val previousFragment: LiveData<Int>
        get() = _previousFragment

    private val bloodPressureDataSource =
        BloodPressureDatabase.getInstance(application).bloodPressureDatabaseDao

    // The person list to show in the pick person spinner
    private lateinit var _personList: LiveData<List<String>>
    val personList: LiveData<List<String>>
        get() = _personList

    var deferredPersonList: Deferred<Unit> = viewModelScope.async(Dispatchers.IO) {
        _personList = bloodPressureDataSource.getAllPersonId()
    }

    // The variable that holds the visibility state of the bottom app bar
    private val _bottomAppBarIsVisible = MutableLiveData<Boolean>()
    val bottomAppBarIsVisible: LiveData<Boolean>
        get() = _bottomAppBarIsVisible

    // The variable that holds the current selected person
    private val _currentSelectedPersonId = MutableLiveData<String>()
    val currentSelectedPersonId: LiveData<String>
        get() = _currentSelectedPersonId


    // Temporary variables
    lateinit var temp: String
    lateinit var job: Job
    lateinit var job1: Job
    lateinit var job2: Job
    lateinit var temp1: LiveData<List<BloodPressure>>
    lateinit var temp2: LiveData<List<BloodPressure>>
    lateinit var bloodPressureAllDataOnePerson: LiveData<List<BloodPressure>>

    /**
     * Initializes the current selected person's Id to the first row of the
     * blood pressure database
     */
    suspend fun initializeCurrentSelectedPersonId() {
        job = viewModelScope.launch(Dispatchers.IO) {
            temp = bloodPressureDataSource.getFirstPersonId()
        }
        job.join()
        _currentSelectedPersonId.postValue(temp)
    }

    /**
     * Initializes the person in the first row's data to the bloodPressureAllDataOnePerson
     * variable so that the variable will not be null
     */
    fun initBloodPressureAllDataOnePerson() {
        job1 = viewModelScope.launch(Dispatchers.IO) {
            currentSelectedPersonId.value?.let {
                temp1 = bloodPressureDataSource.getAllForPerson(it)
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            job1.join()
            bloodPressureAllDataOnePerson = temp1
        }
    }

    fun setBloodPressureAllDataOnePerson() {
        job2 = viewModelScope.launch(Dispatchers.IO) {
            currentSelectedPersonId.value?.let {
                temp2 = bloodPressureDataSource.getAllForPerson(it)
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            job2.join()
            bloodPressureAllDataOnePerson = temp2
        }
    }



    /**
     * Changes the bottom app bar from visible to invisible and vice versa
     * @param state the state to change to
     * @author Tan Yanbo
     */
    fun changeAppBarVisibility(state: Boolean) {
        _bottomAppBarIsVisible.value = state
    }

    /**
     * Changes the current selected person
     * @param personId person_id of the current selected person
     */
    fun changeCurrentSelectedPerson(personId: String) {
        _currentSelectedPersonId.value = personId
    }

    /**
     * Updates the previousFragment value so the add data fragment knows what to draw
     * @param fragmentMenuId The id for the individual menu items, e.g. R.id.glucose,
     * R.id.bloodPressure etc.
     */
    fun updatePreviousFragment(fragmentMenuId: Int) {
        when (fragmentMenuId) {
            R.id.bloodPressure -> _previousFragment.value = 1
            R.id.heightWeight -> _previousFragment.value = 2
            R.id.glucose -> _previousFragment.value = 3
            R.id.person -> _previousFragment.value = 4
            else -> _previousFragment.value = 0
        }
    }

    /**
     * Initialize the previous fragment value to 1 so that the add data fragment knows
     * that the user is entering blood pressure data if the first button the user clicks
     * after he enters the app is the fab
     */
    fun initializePreviousFragment() {
        _previousFragment.value = 1
    }

    /**
     * Provides a View Model Factory to construct the View Model
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(com.example.helloworldapp.healthtracker.viewModel.ViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}