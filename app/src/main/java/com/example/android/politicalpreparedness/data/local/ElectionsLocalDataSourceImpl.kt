package com.example.android.politicalpreparedness.data.local

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.ResultConstants
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.map
import java.lang.Exception

class ElectionsLocalDataSourceImpl(database: ElectionDatabase, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ElectionsLocalDataSource {
    private val electionDao = database.electionDao

    override fun getAllElections(): LiveData<Result<List<Election>>> {
        return electionDao.getAllElections().map {
            Result.Success(it)
        }
    }

    override suspend fun saveElection(election: Election) = withContext(dispatcher) {
        electionDao.insertElection(election)
    }

    override suspend fun getElectionById(electionId: Int): Result<Election> {
        return withContext(dispatcher) {
            try {
                val election = electionDao.getElectionById(electionId)
                if (election != null) {
                    return@withContext Result.Success(election)
                } else {
                    return@withContext Result.Error(Exception(ResultConstants.ELECTION_NOT_FOUND))
                }
            } catch (exception: Exception) {
                return@withContext Result.Error(exception)
            }
        }
    }

    override suspend fun deleteElectionById(electionId: Int) {
        withContext(dispatcher) {
            electionDao.deleteElection(electionId)
        }
    }

    override suspend fun deleteAllElections() {
        withContext(dispatcher) {
            electionDao.clear()
        }
    }
}