package com.example.helloworldapp.healthtracker.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.helloworldapp.healthtracker.R
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressureDatabase
import com.example.helloworldapp.healthtracker.database.glucose.GlucoseDatabase
import com.example.helloworldapp.healthtracker.database.heightWeight.HeightWeightDatabase
import kotlinx.coroutines.*


/**
 * Dialog class to delete one person
 * @param deleteMessageStringResource The string resource of the message to show
 * @param personId The person_id of the person to delete
 */
class DeleteOnePerson(private val deleteMessageString: String, val personId: String): AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_dialog_title)
            .setMessage(deleteMessageString)
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
        val datasourceHeightWeight = HeightWeightDatabase.getInstance(application).heightWeightDatabaseDao
        val datasourceBloodPressure = BloodPressureDatabase.getInstance(application).bloodPressureDatabaseDao
        val datasourceGlucose = GlucoseDatabase.getInstance(application).glucoseDatabaseDao

        uiScope.launch {
            withContext(Dispatchers.IO) {
                datasourceHeightWeight.deletePerson(personId)
                datasourceBloodPressure.deletePerson(personId)
                datasourceGlucose.deletePerson(personId)
            }
        }
        requireActivity().title = getString(R.string.app_name)
    }
}