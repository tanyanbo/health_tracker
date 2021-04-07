package com.example.helloworldapp.healthtracker.database.bloodPressure

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.*

@Entity(tableName="blood_pressure_database")
data class BloodPressure(

    @PrimaryKey(autoGenerate=true)
    val id: Long = 0L,

    @ColumnInfo(name = "person_id")
    val personId: String = "test",

    @ColumnInfo(name="high_bp")
    val highBP: String = "120",

    @ColumnInfo(name="low_bp")
    val lowBP: String = "70",

    @ColumnInfo(name="heart_rate")
    val heartRate: String = "70",

    @ColumnInfo(name="Date")
    val date: String = DateFormat.getDateInstance().format(Calendar.getInstance().time),

    @ColumnInfo(name="Time")
    val time: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "isShown")
    val isShown: Boolean = true
)