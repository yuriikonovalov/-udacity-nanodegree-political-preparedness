package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.SavedElection

@Dao
interface SavedElectionDao {

    @Query("SELECT id FROM saved_elections_table")
    fun observeSavedElectionIds(): LiveData<List<Int>>

    @Query("SELECT id FROM saved_elections_table WHERE id = :electionId")
    fun observeId(electionId: Int): LiveData<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedElection(savedElection: SavedElection)

    @Query("DELETE FROM saved_elections_table WHERE id = :electionId")
    suspend fun deleteSavedElectionById(electionId: Int)
}