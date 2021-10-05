package com.example.android.politicalpreparedness.data.local

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.map
import com.example.android.politicalpreparedness.network.models.SavedElection
import java.lang.Exception

class LocalDataSourceImpl(
        database: ElectionDatabase,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalDataSource {

    private val electionDao = database.electionDao
    private val savedElectionDao = database.savedElectionDao

    override fun observeAllElections(): LiveData<Result<List<Election>>> {
        return electionDao.observeAllElections().map {
            Result.Success(it)
        }
    }

    override suspend fun saveElection(election: Election) {
        withContext(dispatcher) {
            electionDao.insertElection(election)
        }
    }

    override suspend fun saveElections(elections: List<Election>): List<Long> {
        return withContext(dispatcher) {
            return@withContext electionDao.insertElections(elections)
        }
    }

    override fun observeSavedElectionIds(): LiveData<List<Int>> {
        return savedElectionDao.observeSavedElectionIds()
    }

    override fun observeSavedElections(idList: List<Int>): LiveData<Result<List<Election>>> {
        return electionDao.observeSavedElections(idList).map {
            Result.Success(it)
        }
    }

    override fun observeSavedId(electionId: Int): LiveData<Int?> {
        return savedElectionDao.observeId(electionId)
    }

    override suspend fun insertSavedElection(savedElection: SavedElection) {
        withContext(dispatcher) {
            savedElectionDao.insertSavedElection(savedElection)
        }
    }

    override suspend fun getElectionById(electionId: Int): Result<Election> {
        return withContext(dispatcher) {
            try {
                val election = electionDao.getElectionById(electionId)
                if (election != null) {
                    return@withContext Result.Success(election)
                } else {
                    return@withContext Result.Error(Exception())
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

    override suspend fun deleteSavedElectionById(electionId: Int) {
        withContext(dispatcher) {
            savedElectionDao.deleteSavedElectionById(electionId)
        }
    }
}