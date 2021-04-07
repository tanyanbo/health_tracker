package com.example.helloworldapp.healthtracker.database.heightWeight

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.helloworldapp.healthtracker.database.bloodPressure.BloodPressure

@Dao
interface HeightWeightDao {
    /**
     * Add a new row of blood pressure data to the database
     */
    @Insert
    fun insert(heightWeightData: HeightWeight)

    /**
     *  Delete a row
     *  @param heightWeightData data to delete
     */
    @Delete
    fun delete(heightWeightData: HeightWeight)

    /**
     *  Update a row
     *  @param heightWeightData data to update
     */
    @Update
    fun update(heightWeightData: HeightWeight)

    /**
     * Delete all rows
     */
    @Query("DELETE FROM height_weight_database")
    fun deleteAll()


    @Query("SELECT * from height_weight_database WHERE id = :key")
    fun get(key: Long): HeightWeight

    /**
     * Get all the data, no longer useful after adding multiple people
     */
    @Query("SELECT * FROM height_weight_database")
    fun getAll(): LiveData<List<HeightWeight>>

    /**
     * Get the list of people to show in the person chooser spinner.
     */
    @Query("SELECT DISTINCT person_id FROM height_weight_database")
    fun getAllPersonId(): List<String>

    /**
     * Get all the data for one person except the first row (which is the row
     * to tell the database about the new person and not a real record) to show
     * in the show data fragment
     * @param personId The person to get all the data
     */
    @Query("SELECT * FROM height_weight_database WHERE person_id = :personId ORDER BY id DESC LIMIT (SELECT COUNT(id) FROM height_weight_database WHERE person_id = :personId) - 1")
    fun getAllForPerson(personId: String): LiveData<List<HeightWeight>>

    /**
     * Gets the data for one person and sort it descending by data. isShown refers to
     * whether or not the row is only a placeholder
     * @param personId the personId value of the person whose data is going to be shown
     */
    @Query("SELECT * FROM height_weight_database WHERE person_id = :personId AND isShown = 1 ORDER BY Time DESC")
    fun getAllForPersonSortDateDesc(personId: String): LiveData<List<HeightWeight>>

    /**
     * Gets the data for one person and sort it ascending by data. isShown refers to
     * whether or not the row is only a placeholder
     * @param personId the personId value of the person whose data is going to be shown
     */
    @Query("SELECT * FROM height_weight_database WHERE person_id = :personId AND isShown = 1 ORDER BY Time ASC ")
    fun getAllForPersonSortDateAsc(personId: String): LiveData<List<HeightWeight>>

    /**
     * Delete one person from the database
     * @param personId the personId value of the person whose data is about to be deleted
     */
    @Query("DELETE FROM height_weight_database WHERE person_id = :personId")
    fun deletePerson(personId: String)
}