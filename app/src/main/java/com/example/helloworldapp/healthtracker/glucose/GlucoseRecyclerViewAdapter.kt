package com.example.helloworldapp.healthtracker.glucose

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
import com.example.helloworldapp.healthtracker.bloodPressure.BloodPressureDiffCallback
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure
import com.example.helloworldapp.healthtracker.database.glucose.Glucose
import com.example.helloworldapp.healthtracker.heightWeight.HeightWeightRecyclerViewAdapter
import java.text.SimpleDateFormat

/**
 * Recycler view adapter
 * @param listener this parameter is an object that implements the OnItemCLickListener
 * interface
 */
class GlucoseRecyclerViewAdapter(val listener: OnItemClickListener) :
    ListAdapter<Glucose, GlucoseRecyclerViewAdapter.ViewHolder>(GlucoseDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.glucose_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        // initializes all the views in one row of the recycler view
        val glucose: TextView = itemView.findViewById(R.id.tvG)
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
                    listener.onItemClick(bp, adapterPosition)

                }
            }
        }

        /**
         * instructions on how to draw each row
         */
        fun bind(item: Glucose) {
            glucose.text = item.glucose
            date.text = item.date
            time.text = SimpleDateFormat("HH:mm:ss").format(item.time)

            // set various texts to red if it is higher than a certain value
//            if (!item.highBP.isNullOrEmpty()) {
//                if (item.highBP.toInt() > 140) {
//                    highBP.setTextColor(Color.RED)
//                } else {
//                    highBP.setTextColor(Color.BLACK)
//                }
//            }
        }
    }


    /**
     *  An activity or fragment that instantiates the ViewDataAdapter class
     *  will have to implement this interface and define the onClick method
     *  for the delete icon in each row of the Recycler view
     */
    interface OnItemClickListener {
        fun onItemClick(g: Glucose, adapterPosition: Int)
    }
}

/**
 * Checks the difference between two lists so that the Recycler view knows
 * what changed
 */
class GlucoseDiffCallback : DiffUtil.ItemCallback<Glucose>() {
    override fun areItemsTheSame(oldItem: Glucose, newItem: Glucose): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Glucose, newItem: Glucose): Boolean {
        return oldItem == newItem
    }

}