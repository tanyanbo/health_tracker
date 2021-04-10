package com.example.helloworldapp.healthtracker.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabase
import com.example.helloworldapp.healthtracker.database.glucose.Glucose
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabase
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabase
import kotlinx.coroutines.*
import java.lang.NullPointerException

class ViewModel(application: Application) : AndroidViewModel(application) {

    // variable to store which is the current fragment
    private val _previousFragment = MutableLiveData<Int>()
    val previousFragment: LiveData<Int>
        get() = _previousFragment

    private val bloodPressureDataSource =
        BloodPressureDatabase.getInstance(application).bloodPressureDatabaseDao
    private val glucoseDataSource = GlucoseDatabase.getInstance(application).glucoseDatabaseDao
    private val heightWeightDataSource =
        HeightWeightDatabase.getInstance(application).heightWeightDatabaseDao

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

    // The variable that holds whether or not to navigate to the add person fragment
    private val _navigateToAddPerson = MutableLiveData<Boolean>()
    val navigateToAddPerson: LiveData<Boolean>
        get() = _navigateToAddPerson


    // The variable that holds whether or not to navigate to the blood pressure fragment
    // when the app starts
    private val _navigateToBloodPressure = MutableLiveData<Boolean>()
    val navigateToBloodPressure: LiveData<Boolean>
        get() = _navigateToBloodPressure

    // This variable holds whether or not to change the bottom navigation bar selected
    // id to the blood pressure fragment. This is used to by the choose person fragment
    // to tell the main activity to change the selected id from change person to blood
    // pressure
    private val _changeNavBarSelected = MutableLiveData<Boolean>()
    val changeNavBarSelected: LiveData<Boolean>
        get() = _changeNavBarSelected

    // This variable holds whether or not the title should be set to the app name
    // It should be set to the app name after a person is deleted so that the user
    // will not be confused as to whose data is on screen right now
    private val _titleToAppName = MutableLiveData<Boolean>()
    val titleToAppName: LiveData<Boolean>
        get() = _titleToAppName

    fun setTitleToAppName() {
        _titleToAppName.value = true
    }

    fun doneSetTitleToAppName() {
        _titleToAppName.value = false
    }

    fun changeNavBarSelectedFunction() {
        _changeNavBarSelected.value = true
    }

    fun doneChangeNavBarSelected() {
        _changeNavBarSelected.value = false
    }

    lateinit var glucoseAllDataDesc: LiveData<List<Glucose>>
    lateinit var glucoseAllDataAsc: LiveData<List<Glucose>>

    /**
     * changes allDataDesc to be a list of data of one person sorted by descending date
     */
    fun glucoseOrderByDateDesc() {
        glucoseAllDataDesc = glucoseDataSource.getAllForPersonSortDateDesc(_currentSelectedPersonId.value!!)
    }

    /**
     * changes allDataAsc to be a list of data of one person sorted by ascending date
     */
    fun glucoseOrderByDateAsc() {
        glucoseAllDataAsc = glucoseDataSource.getAllForPersonSortDateAsc(_currentSelectedPersonId.value!!)
    }

    lateinit var bloodPressureAllDataDesc: LiveData<List<BloodPressure>>
    lateinit var bloodPressureAllDataAsc: LiveData<List<BloodPressure>>

    /**
     * changes allDataDesc to be a list of data of one person sorted by descending date
     */
    fun bloodPressureOrderByDateDesc() {
        bloodPressureAllDataDesc = bloodPressureDataSource.getAllForPersonSortDateDesc(_currentSelectedPersonId.value!!)
    }

    /**
     * changes allDataAsc to be a list of data of one person sorted by ascending date
     */
    fun bloodPressureOrderByDateAsc() {
        bloodPressureAllDataAsc = bloodPressureDataSource.getAllForPersonSortDateAsc(_currentSelectedPersonId.value!!)
    }

    lateinit var heightWeightAllDataDesc: LiveData<List<HeightWeight>>
    lateinit var heightWeightAllDataAsc: LiveData<List<HeightWeight>>

    /**
     * changes allDataDesc to be a list of data of one person sorted by descending date
     */
    fun heightWeightOrderByDateDesc() {
        heightWeightAllDataDesc = heightWeightDataSource.getAllForPersonSortDateDesc(_currentSelectedPersonId.value!!)
    }

    /**
     * changes allDataAsc to be a list of data of one person sorted by ascending date
     */
    fun heightWeightOrderByDateAsc() {
        heightWeightAllDataAsc = heightWeightDataSource.getAllForPersonSortDateAsc(_currentSelectedPersonId.value!!)
    }

    /**
     * Call this function when the user does not need to navigate to the add person fragment
     * anymore (because there is already data in the database, so currentSelectedPersonId will
     * not be null)
     */
    fun doneNavigating() {
        _navigateToAddPerson.value = false
    }

    lateinit var job: Job
    lateinit var job2: Job
    lateinit var job3: Job
    lateinit var job4: Job
    lateinit var bloodPressureAllDataOnePerson: LiveData<List<BloodPressure>>
    lateinit var glucoseAllDataOnePerson: LiveData<List<Glucose>>
    lateinit var heightWeightAllDataOnePerson: LiveData<List<HeightWeight>>

    /**
     * Initializes the current selected person's Id to the first row of the
     * blood pressure database
     */
    fun initializeCurrentSelectedPersonId() {
        job = viewModelScope.launch(Dispatchers.IO) {
            _currentSelectedPersonId.postValue(bloodPressureDataSource.getFirstPersonId())
        }
    }

    /**
     * If bloodPressureAllDataOnePerson is not null then we will get all the data
     * for the currentSelectedPerson and show his data on the screen, however if
     * it is null then we will tell the main activity to navigate to the
     * add person fragment
     */
    fun setAllData() {

        job2 = viewModelScope.launch(Dispatchers.IO) {
            try {
                bloodPressureAllDataOnePerson =
                    bloodPressureDataSource.getAllForPerson(_currentSelectedPersonId.value!!)
                _navigateToBloodPressure.postValue(true)
            } catch (e: NullPointerException) {
                _navigateToAddPerson.postValue(true)
                _navigateToBloodPressure.postValue(false)
                Log.i("MainActivity", "catch null pointer exception block executed")
            }
        }

    }

    /**
     * Gets all the glucose data for one person so the fragment can show it
     */
    fun setGlucoseData() {
        job3 = viewModelScope.launch(Dispatchers.IO) {
            glucoseAllDataOnePerson =
                glucoseDataSource.getAllForPerson(_currentSelectedPersonId.value!!)
        }
    }

    /**
     * Gets all the height and weight data for one person so the fragment can show it
     */
    fun setHeightWeightData() {
        job4 = viewModelScope.launch(Dispatchers.IO) {
            heightWeightAllDataOnePerson =
                heightWeightDataSource.getAllForPerson(_currentSelectedPersonId.value!!)
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