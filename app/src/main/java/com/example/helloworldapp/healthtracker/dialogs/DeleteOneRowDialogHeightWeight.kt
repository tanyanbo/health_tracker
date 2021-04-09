package com.example.helloworldapp.healthtracker.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.glucose.Glucose
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabase
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeight
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabase
import kotlinx.coroutines.*

class DeleteOneRowDialogHeightWeight(val hw: HeightWeight): AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_message_one_row)
            .setPositiveButton(R.string.delete_dialog_positive_button) { _: DialogInterface, _: Int ->
                confirm()
            }
            .setNegativeButton(R.string.delete_dialog_negative_button) { _: DialogInterface, _: Int -> }
        return builder.create()
    }

    fun confirm() {
        val viewJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewJob)
        val application = requireActivity().applicationContext
        val datasource = HeightWeightDatabase.getInstance(application).heightWeightDatabaseDao

        uiScope.launch {
            withContext(Dispatchers.IO) {
                datasource.delete(hw)
            }
        }
    }
}