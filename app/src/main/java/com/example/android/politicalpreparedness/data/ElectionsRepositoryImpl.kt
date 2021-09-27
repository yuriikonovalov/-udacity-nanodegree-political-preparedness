package com.example.android.politicalpreparedness.data

import com.example.android.politicalpreparedness.data.local.ElectionsLocalDataSource
import com.example.android.politicalpreparedness.data.local.ElectionsLocalDataSourceImpl
import com.example.android.politicalpreparedness.data.remote.ElectionsRemoteDataSource
import com.example.android.politicalpreparedness.data.remote.ElectionsRemoteDataSourceImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepositoryImpl(private val electionsLocalDataSource: ElectionsLocalDataSource,
                              private val electionsRemoteDataSource: ElectionsRemoteDataSource,
                              private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ElectionsRepository {

    override suspend fun refreshElections() {
        withContext(dispatcher) {
            val elections = electionsRemoteDataSource.getElections()
            if (elections is Result.Success) {
                elections.data.forEach {
                    electionsLocalDataSource.saveElection(it)
                }
            }
        }
    }


}