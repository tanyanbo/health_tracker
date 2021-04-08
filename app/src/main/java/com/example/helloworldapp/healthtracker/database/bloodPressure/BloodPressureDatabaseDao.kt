package com.example.helloworldapp.healthtracker.database.bloodPressure

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BloodPressureDatabaseDao {
    /**
     * Add a new row of blood pressure data to the database
     */
    @Insert
    fun insert(bpData: BloodPressure)

    /**
     *  Delete a row
     *  @param bpData data to delete
     */
    @Delete
    fun delete(bpData: BloodPressure)

    /**
     *  Update a row
     *  @param bpData data to update
     */
    @Update
    fun update(bpData: BloodPressure)

    /**
     * Delete all rows
     */
    @Query("DELETE FROM blood_pressure_database")
    fun deleteAll()


    @Query("SELECT * from blood_pressure_database WHERE id = :key")
    fun get(key: Long): BloodPressure

    /**
     * Get all the data, no longer useful after adding multiple people
     */
    @Query("SELECT * FROM blood_pressure_database")
    fun getAll(): LiveData<List<BloodPressure>>

    /**
     * Get the list of people to show in the person chooser spinner.
     */
    @Query("SELECT DISTINCT person_id FROM blood_pressure_database")
    fun getAllPersonId(): LiveData<List<String>>

    /**
     * Get all the data for one person except the first row (which is the row
     * to tell the database about the new person and not a real record) to show
     * in the show data fragment
     * @param personId The person to get all the data
     */
    @Query("SELECT * FROM blood_pressure_database WHERE person_id = :personId ORDER BY id DESC LIMIT (SELECT COUNT(id) FROM blood_pressure_database WHERE person_id = :personId) - 1")
    fun getAllForPerson(personId: String): LiveData<List<BloodPressure>>

    /**
     * Gets the data for one person and sort it descending by data. isShown refers to
     * whether or not the row is only a placeholder
     * @param personId the personId value of the person whose data is going to be shown
     */
    @Query("SELECT * FROM blood_pressure_database WHERE person_id = :personId AND isShown = 1 ORDER BY Time DESC")
    fun getAllForPersonSortDateDesc(personId: String): LiveData<List<BloodPressure>>

    /**
     * Gets the data for one person and sort it ascending by data. isShown refers to
     * whether or not the row is only a placeholder
     * @param personId the personId value of the person whose data is going to be shown
     */
    @Query("SELECT * FROM blood_pressure_database WHERE person_id = :personId AND isShown = 1 ORDER BY Time ASC ")
    fun getAllForPersonSortDateAsc(personId: String): LiveData<List<BloodPressure>>

    /**
     * Delete one person from the database
     * @param personId the personId value of the person whose data is about to be deleted
     */
    @Query("DELETE FROM blood_pressure_database WHERE person_id = :personId")
    fun deletePerson(personId: String)

    @Query("SELECT person_id FROM blood_pressure_database ORDER BY id ASC LIMIT 1")
    fun getFirstPersonId(): String
}