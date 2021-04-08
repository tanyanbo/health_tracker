package com.example.helloworldapp.healthtracker.bloodPressure

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import java.text.SimpleDateFormat

/**
 * Recycler view adapter
 * @param listener this parameter is an object that implements the OnItemCLickListener
 * interface
 */
class BloodPressureRecyclerViewAdapter(val listener: OnItemClickListener) :
    ListAdapter<BloodPressure, BloodPressureRecyclerViewAdapter.ViewHolder>(BloodPressureDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.blood_pressure_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        // initializes all the views in one row of the recycler view
        val highBP: TextView = itemView.findViewById(R.id.tvHBP)
        val lowBP: TextView = itemView.findViewById(R.id.tvLBP)
        val hr: TextView = itemView.findViewById(R.id.tvHR)
        val date: TextView = itemView.findViewById(R.id.tvDt)
        val time: TextView = itemView.findViewById(R.id.tvTm)
        val delIcon: ImageView = itemView.findViewById(R.id.imgDelete)

        // draws the delete icon at every row of the recycler view, this is
        // in the init block because the icon does not change for different rows
        init {
            delIcon.setImageResource(R.drawable.ic_baseline_delete_24)
        }

        // sets click listeners for the delete icon
        init {
            delIcon.setOnClickListener {
                val bp = getItem(adapterPosition)
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(bp)
                }
            }
        }

        /**
         * instructions on how to draw each row
         */
        fun bind(item: BloodPressure) {
            highBP.text = item.highBP
            lowBP.text = item.lowBP
            hr.text = item.heartRate
            date.text = item.date
            time.text = SimpleDateFormat("HH:mm:ss").format(item.time)

            // set various texts to red if it is higher than a certain value
            if (!item.highBP.isNullOrEmpty()) {
                if (item.highBP.toInt() > 140) {
                    highBP.setTextColor(Color.RED)
                } else {
                    highBP.setTextColor(Color.BLACK)
                }
            }

            if (!item.lowBP.isNullOrEmpty()) {
                if (item.lowBP.toInt() > 90) {
                    lowBP.setTextColor(Color.RED)
                } else {
                    lowBP.setTextColor(Color.BLACK)
                }
            }

            if (!item.heartRate.isNullOrEmpty()) {
                if (item.heartRate.toInt() > 100) {
                    hr.setTextColor(Color.RED)
                } else {
                    hr.setTextColor(Color.BLACK)
                }
            }
        }
    }


    /**
     *  An activity or fragment that instantiates the ViewDataAdapter class
     *  will have to implement this interface and define the onClick method
     *  for the delete icon in each row of the Recycler view
     */
    interface OnItemClickListener {
        fun onItemClick(bp: BloodPressure)
    }
}

/**
 * Checks the difference between two lists so that the Recycler view knows
 * what changed
 */
class BloodPressureDiffCallback : DiffUtil.ItemCallback<BloodPressure>() {
    override fun areItemsTheSame(oldItem: BloodPressure, newItem: BloodPressure): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: BloodPressure, newItem: BloodPressure): Boolean {
        return oldItem == newItem
    }

}