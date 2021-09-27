package com.example.android.politicalpreparedness.data.local

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Election

interface ElectionsLocalDataSource {
    fun getAllElections(): LiveData<Result<List<Election>>>

    suspend fun saveElection(election: Election)

    suspend fun getElectionById(electionId: Int): Result<Election>

    suspend fun deleteElectionById(electionId: Int)

    suspend fun deleteAllElections()
}