package com.example.helloworldapp.healthtracker.database.heightWeight

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.*

@Entity(tableName="height_weight_database")
data class HeightWeight(

    @PrimaryKey(autoGenerate=true)
    val id: Long = 0L,

    @ColumnInfo(name = "person_id")
    val personId: String = "test",

    @ColumnInfo(name="high_bp")
    val height: String = "170",

    @ColumnInfo(name="low_bp")
    val weight: String = "70",

    @ColumnInfo(name="Date")
    val date: String = DateFormat.getDateInstance().format(Calendar.getInstance().time),

    @ColumnInfo(name="Time")
    val time: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "isShown")
    val isShown: Boolean = true
)