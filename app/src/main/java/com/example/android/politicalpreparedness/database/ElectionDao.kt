package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.SavedElection

@Dao
interface ElectionDao {

    //1TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElection(election: Election)

    //Add select all election query
    @Query("SELECT * FROM election_table")
    fun observeAllElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id IN (:idList)")
    fun observeSavedElections(idList: List<Int>): LiveData<List<Election>>

    // Add select single election query
    @Query("SELECT * FROM election_table WHERE id =:electionId")
    suspend fun getElectionById(electionId: Int): Election?

    // Add delete query
    @Query("DELETE FROM election_table WHERE id =:electionId")
    suspend fun deleteElection(electionId: Int)

    // Add clear query
    @Query("DELETE FROM election_table ")
    suspend fun clear()

}