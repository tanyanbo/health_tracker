package com.example.helloworldapp.healthtracker.database.glucose

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure

@Dao
interface GlucoseDatabaseDao {
    /**
     * Add a new row of blood pressure data to the database
     */
    @Insert
    fun insert(glucoseData: Glucose)

    /**
     *  Delete a row
     *  @param glucoseData data to delete
     */
    @Delete
    fun delete(glucoseData: Glucose)

    /**
     *  Update a row
     *  @param glucoseData data to update
     */
    @Update
    fun update(glucoseData: Glucose)

    /**
     * Delete all rows
     */
    @Query("DELETE FROM glucose_database WHERE isShown = 1")
    fun deleteAll()


    @Query("SELECT * from glucose_database WHERE id = :key")
    fun get(key: Long): Glucose

    /**
     * Get all the data, no longer useful after adding multiple people
     */
    @Query("SELECT * FROM glucose_database")
    fun getAll(): LiveData<List<Glucose>>

    /**
     * Get the list of people to show in the person chooser spinner.
     */
    @Query("SELECT DISTINCT person_id FROM glucose_database")
    fun getAllPersonId(): List<String>

    /**
     * Get all the data for one person except the first row (which is the row
     * to tell the database about the new person and not a real record) to show
     * in the show data fragment
     * @param personId The person to get all the data
     */
    @Query("SELECT * FROM glucose_database WHERE person_id = :personId AND isShown = 1 ORDER BY id DESC")
    fun getAllForPerson(personId: String): LiveData<List<Glucose>>

    /**
     * Gets the data for one person and sort it descending by data. isShown refers to
     * whether or not the row is only a placeholder
     * @param personId the personId value of the person whose data is going to be shown
     */
    @Query("SELECT * FROM glucose_database WHERE person_id = :personId AND isShown = 1 ORDER BY Time DESC")
    fun getAllForPersonSortDateDesc(personId: String): LiveData<List<Glucose>>

    /**
     * Gets the data for one person and sort it ascending by data. isShown refers to
     * whether or not the row is only a placeholder
     * @param personId the personId value of the person whose data is going to be shown
     */
    @Query("SELECT * FROM glucose_database WHERE person_id = :personId AND isShown = 1 ORDER BY Time ASC ")
    fun getAllForPersonSortDateAsc(personId: String): LiveData<List<Glucose>>

    /**
     * Delete one person from the database
     * @param personId the personId value of the person whose data is about to be deleted
     */
    @Query("DELETE FROM glucose_database WHERE person_id = :personId")
    fun deletePerson(personId: String)

    @Query("SELECT MAX(time) FROM glucose_database")
    fun getLatestTime(): Long
}