package com.example.helloworldapp.healthtracker.database.glucose

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.*

@Entity(tableName="glucose_database")
data class Glucose(

    @PrimaryKey(autoGenerate=true)
    val id: Long = 0L,

    @ColumnInfo(name = "person_id")
    val personId: String = "test",

    @ColumnInfo(name="glucose")
    val glucose: String = "6.5",

    @ColumnInfo(name = "before_after_food")
    val beforeAfterFood: String = "before",

    @ColumnInfo(name="Date")
    val date: String = DateFormat.getDateInstance().format(Calendar.getInstance().time),

    @ColumnInfo(name="Time")
    val time: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "isShown")
    val isShown: Boolean = true
)