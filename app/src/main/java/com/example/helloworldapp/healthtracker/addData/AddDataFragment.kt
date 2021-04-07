package com.example.helloworldapp.healthtracker.addData

import android.opengl.Visibility
import android.os.Bundle
import android.view.ActionProvider
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.databinding.FragmentAddDataBinding
import com.example.helloworldapp.healthtracker.viewModel.ViewModel


class AddDataFragment : Fragment() {

    private lateinit var binding: FragmentAddDataBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_data, container, false)

        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        /**
         * This is deciding how to draw the edit text boxes, for example it will draw only
         * one edit text for glucose fragment since the user only needs to enter glucose,
         * for blood pressure it will draw three edit text boxes for high bp, low bp and heart
         * rate respectively.
         */
        viewModel.previousFragment.observe(viewLifecycleOwner, Observer {
            when (it) {
                2 -> {
                    binding.etBox1.hint = getString(R.string.enter_height)
                    binding.etBox2.hint = getString(R.string.enter_weight)
                    binding.etBox3.visibility = View.GONE
                }
                3 -> {
                    binding.etBox1.hint = getString(R.string.enter_glucose)
                    binding.etBox2.visibility = View.GONE
                    binding.etBox3.visibility = View.GONE
                }
            }
        })
        return binding.root
    }


}