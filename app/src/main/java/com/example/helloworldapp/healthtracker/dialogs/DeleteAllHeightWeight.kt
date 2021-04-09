package com.example.helloworldapp.healthtracker.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabase
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabase
import kotlinx.coroutines.*

class DeleteAllHeightWeight(private val deleteMessageStringResource: Int): AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title)
            .setMessage(deleteMessageStringResource)
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
                datasource.deleteAll()
            }
        }
    }
}