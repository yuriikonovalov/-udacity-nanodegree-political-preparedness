package com.example.android.politicalpreparedness.data.remote

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.ResultConstants
import com.example.android.politicalpreparedness.network.CivicsApiService
import java.lang.Exception

class ElectionsRemoteDataSourceImpl(private val electionsApi: CivicsApiService) : ElectionsRemoteDataSource {

    override suspend fun getElections(): Result<List<Election>> {
        val result = electionsApi.getElections()
        return if (result.elections.isEmpty()) {
            Result.Error(Exception(ResultConstants.ELECTIONS_NOT_FOUND))
        } else {
            Result.Success(result.elections)
        }
    }

}