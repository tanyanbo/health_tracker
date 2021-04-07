package com.example.helloworldapp.healthtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.helloworldapp.healthtracker.addPerson.AddPersonFragment
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureFragment
import com.example.helloworldapp.healthtracker.databinding.ActivityMainBinding
import com.example.helloworldapp.healthtracker.glucose.GlucoseFragment
import com.example.helloworldapp.healthtracker.heightWeight.HeightWeightFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bloodPressureFragment: BloodPressureFragment
    private lateinit var glucoseFragment: GlucoseFragment
    private lateinit var heightWeightFragment: HeightWeightFragment
    private lateinit var addPersonFragment: AddPersonFragment
    private lateinit var addDataFragment: AddDataFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavBar.background = null

        bloodPressureFragment = BloodPressureFragment()
        glucoseFragment = GlucoseFragment()
        heightWeightFragment = HeightWeightFragment()
        addPersonFragment = AddPersonFragment()
        addDataFragment = AddDataFragment()

        navigateToFragment(bloodPressureFragment)

        binding.fab.setOnClickListener {
//            supportFragmentManager.beginTransaction().apply {
//                replace(R.id.frameLayout, addDataFragment)
//                setCustomAnimations(R.anim.scale, R.anim.scale_exit)
//                addToBackStack(null)
//                commit()
//            }

            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.scale, R.anim.scale_exit)
                .replace(R.id.frameLayout, addDataFragment).addToBackStack(null).commit()
        }

        binding.bottomNavBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.glucose -> navigateToFragment(glucoseFragment)
                R.id.bloodPressure -> navigateToFragment(bloodPressureFragment)
                R.id.heightWeight -> navigateToFragment(heightWeightFragment)
                R.id.person-> navigateToFragment(addPersonFragment)
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
}