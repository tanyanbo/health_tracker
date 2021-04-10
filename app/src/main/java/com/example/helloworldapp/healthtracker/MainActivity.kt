package com.example.helloworldapp.healthtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.helloworldapp.healthtracker.addData.AddDataFragment
import com.example.helloworldapp.healthtracker.addPerson.AddPersonFragment
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureFragment
import com.example.helloworldapp.healthtracker.choosePerson.ChoosePersonFragment
import com.example.helloworldapp.healthtracker.databinding.ActivityMainBinding
import com.example.helloworldapp.healthtracker.glucose.GlucoseFragment
import com.example.helloworldapp.healthtracker.heightWeight.HeightWeightFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bloodPressureFragment: BloodPressureFragment
    private lateinit var glucoseFragment: GlucoseFragment
    private lateinit var heightWeightFragment: HeightWeightFragment
    private lateinit var addPersonFragment: AddPersonFragment
    private lateinit var addDataFragment: AddDataFragment
    private lateinit var choosePersonFragment: ChoosePersonFragment

    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavBar.background = null

        bloodPressureFragment = BloodPressureFragment()
        glucoseFragment = GlucoseFragment()
        heightWeightFragment = HeightWeightFragment()
        addPersonFragment = AddPersonFragment()
        addDataFragment = AddDataFragment()
        choosePersonFragment = ChoosePersonFragment()


        val viewModelFactory =
            com.example.helloworldapp.healthtracker.viewModel.ViewModel.Factory(this.application)
        val viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(com.example.helloworldapp.healthtracker.viewModel.ViewModel::class.java)

        /**
         * This will receive information from the view model if it needs to navigate to the
         * add person fragment
         */
        viewModel.navigateToAddPerson.observe(this, Observer {
            Log.i(TAG, "navigateToAddPerson live data executed: $it")
            if (it == true) {
                navigateToFragment(addPersonFragment)
                Log.i(TAG, "navigated to add person")
                viewModel.doneNavigating()
            }
        })

        /**
         * Initializes the previous fragment value to 1. This is just so that it is not null
         */
        viewModel.initializePreviousFragment()

        /**
         * Initialize the current selected person. Only after the current selected person is initialized
         * can viewModel.setAllData() be called, because it needs the current selected person as a parameter.
         * Current selected person could be null if there is no data in the database which is why we will
         * navigate to the add person fragment
         */
        lifecycleScope.launch {
            viewModel.initializeCurrentSelectedPersonId()
            viewModel.job.join()
            Log.i(TAG, "job 1 done")

            viewModel.setAllData()
            viewModel.job2.join()
            Log.i(TAG, "job 2 done")
            viewModel.navigateToBloodPressure.observe(this@MainActivity, Observer {
                if (it) {

                    navigateToFragment(bloodPressureFragment)
                }
            })
        }


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                viewModel.deferredPersonList.await()
            }

            /**
             * Whenever the personList is empty (either because the user delete
             * every person in the database or through the database insepector
             * in android studio), this observer will get notified and the app will
             * navigate to the add person fragment
             */
            viewModel.personList.observe(this@MainActivity, Observer {
                // checks to see if there is data in the database, if there isn't any data,
                // then navigate to the add person screen
                if (it.isNullOrEmpty()) {
                    for (i in 1..supportFragmentManager.backStackEntryCount) {
                        supportFragmentManager.popBackStack()
                    }
                    supportFragmentManager.beginTransaction().apply {

                        replace(R.id.frameLayout, AddPersonFragment())
                        addToBackStack(null)
                        commit()
                    }
                    viewModel.changeAppBarVisibility(false)

                }
            })
        }

        /**
         * Sets the visibility of the app bar and legend
         */
        viewModel.bottomAppBarIsVisible.observe(this, Observer {
            binding.bottomAppBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.fab.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.tvHigh.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.tvNormal.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        /**
         * After the user navigates from the choose person fragment to the blood pressure
         * fragment, the nav bar selected item will change to blood pressure. The delay
         * is to wait for the transition animation to finish
         */
        viewModel.changeNavBarSelected.observe(this, Observer {
            if (it) {
                lifecycleScope.launch {
                    delay(500)
                    binding.bottomNavBar.selectedItemId = R.id.bloodPressure
                    viewModel.doneChangeNavBarSelected()
                }
            }
        })

        /**
         * Includes the animation logic for opening the add data fragment, if the user clicks on the fab
         * when he is at the first 3 fragments, the app will navigate to the add data fragment, if he is
         * at the choose person fragment, the app will navigate to the add person fragment.
         */
        binding.fab.setOnClickListener {
            when (binding.bottomNavBar.selectedItemId) {
                R.id.person -> {
                    supportFragmentManager.beginTransaction().apply {
                        setCustomAnimations(R.anim.scale, R.anim.scale_exit)
                        replace(R.id.frameLayout, addPersonFragment)
                        addToBackStack(null)
                        commit()
                    }
                }

                else -> {
                    supportFragmentManager.beginTransaction().apply {
                        setCustomAnimations(R.anim.scale, R.anim.scale_exit)
                        replace(R.id.frameLayout, addDataFragment)
                        addToBackStack(null)
                        commit()
                    }
                }
            }
        }


        /**
         * navigates to other fragments on click of the bottom navigation bar,
         * also updates the previous fragment value in the view model so that
         * if the user clicks on the floating action bar the add data fragment
         * will know to draw the correct layout.
         */
        binding.bottomNavBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.glucose -> {
                    navigateToFragment(glucoseFragment)
                    binding.tvNormal.visibility = View.VISIBLE
                    binding.tvHigh.visibility = View.VISIBLE
                    viewModel.updatePreviousFragment(it.itemId)
                }
                R.id.bloodPressure -> {
                    navigateToFragment(bloodPressureFragment)
                    binding.tvNormal.visibility = View.VISIBLE
                    binding.tvHigh.visibility = View.VISIBLE
                    viewModel.updatePreviousFragment(it.itemId)
                }
                R.id.heightWeight -> {
                    navigateToFragment(heightWeightFragment)
                    binding.tvNormal.visibility = View.VISIBLE
                    binding.tvHigh.visibility = View.VISIBLE
                    viewModel.updatePreviousFragment(it.itemId)
                }
                R.id.person -> {
                    navigateToFragment(choosePersonFragment)
                    binding.tvNormal.visibility = View.INVISIBLE
                    binding.tvHigh.visibility = View.INVISIBLE
                    viewModel.updatePreviousFragment(it.itemId)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }


    /**
     * Navigates to a fragment
     * @param fragment The fragment to navigate to
     */
    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "activity destroyed")
    }
}

