package com.example.helloworldapp.healthtracker.viewModel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class ViewModel(application: Application): AndroidViewModel(application) {

    private val _previousFragment = MutableLiveData<Int>()
    val previousFragment: LiveData<Int>
        get() = _previousFragment

     val bloodPressureDataSource = BloodPressureDatabase.getInstance(application).bloodPressureDatabaseDao

    private val _personList = MutableLiveData<List<String>>()
    val personList: LiveData<List<String>> get() = _personList

    var deferredPersonList: Deferred<Unit> = viewModelScope.async(Dispatchers.IO) {
        _personList.value = bloodPressureDataSource.getAllPersonId()
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