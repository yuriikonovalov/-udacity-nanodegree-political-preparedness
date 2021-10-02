package com.example.android.politicalpreparedness.data.remote

import android.util.Log
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.ResultConstants
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import retrofit2.HttpException
import java.lang.Exception

class RemoteDataSourceImpl(private val civicsApiService: CivicsApiService) : RemoteDataSource {

    override suspend fun getElections(): Result<List<Election>> {
        try {
            val result = civicsApiService.getElections()
            return if (result.elections.isEmpty()) {
                Log.d("requestElections", "result - empty")
                Result.Error(Exception(ResultConstants.ELECTIONS_NOT_FOUND))
            } else {
                Log.d("requestElections", "result - success")
                Result.Success(result.elections)
            }
        } catch (e: Exception) {
            Log.d("requestElections", "Exception: ${e.localizedMessage}")
            return Result.Error(e)
        }

    }

    override suspend fun getVoterInfo(address: String, electionId: Long): Result<VoterInfoResponse> {
        return try {
            val result = civicsApiService.getVoterInfo(address, electionId)
            Result.Success(result)
        } catch (e: HttpException) {
            Result.Error(e)
        }
    }


}