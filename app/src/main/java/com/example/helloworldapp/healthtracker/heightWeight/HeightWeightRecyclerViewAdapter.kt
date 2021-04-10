package com.example.helloworldapp.healthtracker.heightWeight

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
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureRecyclerViewAdapter
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import java.text.SimpleDateFormat

/**
 * Recycler view adapter
 * @param listener this parameter is an object that implements the OnItemCLickListener
 * interface
 */
class HeightWeightRecyclerViewAdapter(val listener: OnItemClickListener) :
    ListAdapter<HeightWeight, HeightWeightRecyclerViewAdapter.ViewHolder>(HeightWeightDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.height_weight_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        // initializes all the views in one row of the recycler view
        val height: TextView = itemView.findViewById(R.id.tvH)
        val weight: TextView = itemView.findViewById(R.id.tvW)
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
        fun bind(item: HeightWeight) {
            height.text = item.height
            weight.text = item.weight
            date.text = item.date
            time.text = SimpleDateFormat("HH:mm:ss").format(item.time)

            // set weight to red if bmi is higher than 23
            if (!item.height.isNullOrEmpty() && !item.weight.isNullOrEmpty()) {
                val bmi = item.weight.toFloat() / ((item.height.toFloat() / 100) * (item.height.toFloat() / 100))
                if (bmi > 23.0F) {
                    weight.setTextColor(Color.RED)
                } else {
                    weight.setTextColor(Color.BLACK)
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
        fun onItemClick(hw: HeightWeight)
    }
}

/**
 * Checks the difference between two lists so that the Recycler view knows
 * what changed
 */
class HeightWeightDiffCallback : DiffUtil.ItemCallback<HeightWeight>() {
    override fun areItemsTheSame(oldItem: HeightWeight, newItem: HeightWeight): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: HeightWeight, newItem: HeightWeight): Boolean {
        return oldItem == newItem
    }

}