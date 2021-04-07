package com.example.helloworldapp.healthtracker.choosePerson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.databinding.FragmentChoosePersonBinding


class ChoosePersonFragment : Fragment() {

    private lateinit var binding: FragmentChoosePersonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_person, container, false)

        return binding.root
    }


}