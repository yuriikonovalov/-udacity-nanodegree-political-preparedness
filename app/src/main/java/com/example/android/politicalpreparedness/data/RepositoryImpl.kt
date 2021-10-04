package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.local.LocalDataSource
import com.example.android.politicalpreparedness.data.remote.RemoteDataSource
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.SavedElection
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.RefreshCacheResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryImpl(private val localDataSource: LocalDataSource,
                     private val remoteDataSource: RemoteDataSource,
                     private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : Repository {

    override suspend fun refreshElectionsCache(): RefreshCacheResult {
        return withContext(dispatcher) {
            val elections = remoteDataSource.getElections()
            if (elections is Result.Success) {
                val rowIdList = localDataSource.saveElections(elections.data)
                if (rowIdList.isNotEmpty()) {
                    return@withContext RefreshCacheResult.SUCCESS
                } else {
                    return@withContext RefreshCacheResult.FAILURE
                }
            } else {
                return@withContext RefreshCacheResult.FAILURE
            }
        }
    }

    override fun observeElections(): LiveData<Result<List<Election>>> {
        return localDataSource.observeAllElections()
    }

    override fun observeSavedElections(idList: List<Int>): LiveData<Result<List<Election>>> {
        return localDataSource.observeSavedElections(idList)
    }

    override fun observeSavedElectionsIds(): LiveData<List<Int>> {
        return localDataSource.observeSavedElectionIds()
    }

    override fun observeSavedId(electionId: Int): LiveData<Int?> {
        return localDataSource.observeSavedId(electionId)
    }

    override suspend fun insertSavedElection(savedElection: SavedElection) {
        localDataSource.insertSavedElection(savedElection)
    }

    override suspend fun deleteSavedElection(electionId: Int) {
        localDataSource.deleteSavedElectionById(electionId)
    }

    override suspend fun getVoterInfo(address: String, electionId: Long): Result<VoterInfoResponse> {
        return remoteDataSource.getVoterInfo(address, electionId)
    }

    override suspend fun getRepresentatives(address: String): Result<RepresentativeResponse> {
        return remoteDataSource.getRepresentatives(address)
    }


}