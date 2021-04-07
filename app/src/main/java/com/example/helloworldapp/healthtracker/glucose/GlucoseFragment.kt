package com.example.helloworldapp.healthtracker.glucose

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.databinding.FragmentGlucoseBinding
import com.example.helloworldapp.healthtracker.databinding.FragmentHeightWeightBinding


class GlucoseFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentGlucoseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_glucose, container, false)
        return binding.root
    }


}