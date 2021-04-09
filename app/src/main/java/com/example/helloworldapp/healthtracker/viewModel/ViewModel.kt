package com.example.helloworldapp.healthtracker.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ViewModel(application: Application): AndroidViewModel(application) {

    // variable to store which is the current fragment
    private val _previousFragment = MutableLiveData<Int>()
    val previousFragment: LiveData<Int>
        get() = _previousFragment

    private val bloodPressureDataSource = BloodPressureDatabase.getInstance(application).bloodPressureDatabaseDao

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

    /**
     * Initializes the current selected person's Id to the first row of the
     * blood pressure database
     */
    fun initializeCurrentSelectedPersonId() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentSelectedPersonId.value = bloodPressureDataSource.getFirstPersonId()
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
     * @param personId of the current selected person
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