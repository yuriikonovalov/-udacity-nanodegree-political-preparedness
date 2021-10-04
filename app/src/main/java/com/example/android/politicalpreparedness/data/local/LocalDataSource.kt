package com.example.android.politicalpreparedness.data.local

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.SavedElection

interface LocalDataSource {
    fun observeAllElections(): LiveData<Result<List<Election>>>


    suspend fun saveElection(election: Election)

    suspend fun saveElections(elections: List<Election>): List<Long>

    fun observeSavedElectionIds(): LiveData<List<Int>>

    fun observeSavedElections(idList: List<Int>): LiveData<Result<List<Election>>>

    fun observeSavedId(electionId: Int): LiveData<Int?>

    suspend fun insertSavedElection(savedElection: SavedElection)

    suspend fun getElectionById(electionId: Int): Result<Election>

    suspend fun deleteElectionById(electionId: Int)

    suspend fun deleteAllElections()

    suspend fun deleteSavedElectionById(electionId: Int)
}